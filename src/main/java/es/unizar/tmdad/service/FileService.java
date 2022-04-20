package es.unizar.tmdad.service;

import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

public interface FileService {
    Mono<String> store(MultipartFile file, String name);
}
