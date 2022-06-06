package es.unizar.tmdad.service.impl;

import es.unizar.tmdad.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;


@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Value("${chat.file-api}")
    private String fileApiUrl;
    private final WebClient webClient;
    private final ReactiveCircuitBreaker saveFileCircuitBreaker;

    public FileServiceImpl(ReactiveCircuitBreakerFactory circuitBreakerFactory) {
        this.webClient = WebClient.builder()
                .defaultHeader(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        this.saveFileCircuitBreaker = circuitBreakerFactory.create("slow");
    }

    @Override
    public Mono<String> store(MultipartFile file, String name, String firebaseToken, String firebaseEmail) {
        return saveFileCircuitBreaker.run(
                webClient.post().uri(fileApiUrl + "/")
                        .header("X-Auth-User", firebaseEmail)
                        .header("X-Auth-Firebase", firebaseToken)
                        .body(BodyInserters
                                .fromMultipartData("chat", name)
                                .with("file", file.getResource()))
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.of(100000, ChronoUnit.MILLIS))
        );
    }
}
