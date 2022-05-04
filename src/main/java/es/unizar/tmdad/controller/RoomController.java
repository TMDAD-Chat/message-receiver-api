package es.unizar.tmdad.controller;

import es.unizar.tmdad.controller.exception.UserNotFoundException;
import es.unizar.tmdad.dto.MessageDto;
import org.springframework.web.multipart.MultipartFile;

public interface RoomController {

    void sendNewTextMessage(String groupId, String sender, MessageDto msg) throws UserNotFoundException;
    void sendNewFileMessage(String groupId, String sender, MultipartFile file) throws UserNotFoundException;

}
