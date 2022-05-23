package es.unizar.tmdad.controller;

import es.unizar.tmdad.controller.exception.RoomNotFoundException;
import es.unizar.tmdad.controller.exception.UserNotFoundException;
import es.unizar.tmdad.controller.exception.UserNotInTheRoomException;
import es.unizar.tmdad.dto.MessageDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface RoomController {

    @PostMapping("/{id}/message")
    void sendNewTextMessage(@PathVariable("id") Long userId, MessageDto msg) throws UserNotFoundException, RoomNotFoundException, UserNotInTheRoomException;

    @PostMapping("/{id}/file")
    void sendNewFileMessage(@PathVariable("id") Long userId, @RequestParam("sender") String sender, @RequestParam("file") MultipartFile file) throws UserNotFoundException,UserNotInTheRoomException;

    @GetMapping("/{roomId}/conversation/{mail}")
    void getLastMessagesInRoomFor(@PathVariable("roomId") Long room, @PathVariable("mail") String user) throws UserNotFoundException, RoomNotFoundException,UserNotInTheRoomException;
}
