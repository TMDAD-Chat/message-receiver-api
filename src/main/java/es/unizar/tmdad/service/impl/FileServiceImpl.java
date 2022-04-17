package es.unizar.tmdad.service.impl;

import es.unizar.tmdad.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class FileServiceImpl implements FileService {

    @Value("${chat.file-api}")
    private String fileApiUrl;
    private final WebClient webClient;
    private final ReactiveCircuitBreaker saveFileCircuitBreaker;

    public FileServiceImpl(ReactiveCircuitBreakerFactory circuitBreakerFactory) {
        this.webClient = WebClient.builder()
                .baseUrl(fileApiUrl)
                .defaultHeader(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        this.saveFileCircuitBreaker = circuitBreakerFactory.create("slow");
    }

    @Override
    public Mono<String> store(MultipartFile file, String name) {
        System.out.println(name);
        return saveFileCircuitBreaker.run(
                webClient.post().uri("/")
                        .body(BodyInserters
                                .fromMultipartData("chat", name)
                                .with("file", file.getResource()))
                        .retrieve()
                        .bodyToMono(String.class),
                throwable -> Mono.just("Default image")
        );
    }
}
