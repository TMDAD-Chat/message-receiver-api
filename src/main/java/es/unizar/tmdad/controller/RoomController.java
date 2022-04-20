package es.unizar.tmdad.controller;

import es.unizar.tmdad.dto.MessageDto;
import org.springframework.web.multipart.MultipartFile;

public interface RoomController {

    void sendNewTextMessage(String groupId, MessageDto msg);
    void sendNewFileMessage(String groupId, MultipartFile file);

}
