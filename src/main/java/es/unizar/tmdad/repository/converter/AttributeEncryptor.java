package es.unizar.tmdad.repository.converter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.AttributeConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Component
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private static final String AES = "AES";
    private static final String SECRET = "secret-key-12345";

    private final Key key;
    private final Cipher cipher;
    private final PublicKey publicKey;

    public AttributeEncryptor(@Value("${chat.private-key}") String privateKeyFilename, @Value("${chat.public-key}") String publicKeyFilename) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, CertificateException {
        String privateKeyString = readAndCleanKey(privateKeyFilename, "RSA PRIVATE KEY", true);
        String publicKeyString = readAndCleanKey(publicKeyFilename, "CERTIFICATE", false);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString.getBytes()));

        this.key = kf.generatePrivate(privateSpec);
        this.publicKey = cf.generateCertificate(new ByteArrayInputStream(publicKeyString.getBytes())).getPublicKey();
        this.cipher = Cipher.getInstance("RSA");
    }

    private static String readAndCleanKey(String keyFilename, String header, boolean clean) throws IOException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(keyFilename));
        String keyString = new String(keyBytes);

        if(clean) {
            keyString = keyString.replace("-----BEGIN " + header + "-----\n", "");
            keyString = keyString.replace("-----END " + header + "-----", "");
            keyString = keyString.replaceAll("\n", "");
            keyString = keyString.replaceAll(" ", "");
        }
        return keyString;
    }


    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8)));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            //throw new RuntimeException(e);
            //Attribute is not encrypted!!
            return dbData;
        }
    }


    static {
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
    }
}
