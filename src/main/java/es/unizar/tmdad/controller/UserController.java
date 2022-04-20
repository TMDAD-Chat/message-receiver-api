package es.unizar.tmdad.controller;

import es.unizar.tmdad.controller.exception.*;
import es.unizar.tmdad.dto.MessageDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserController {

    void sendNewTextMessage(String userId, MessageDto msg) throws UserNotFoundException;
    void sendNewFileMessage(String userId, MultipartFile file) throws UserNotFoundException;

}
