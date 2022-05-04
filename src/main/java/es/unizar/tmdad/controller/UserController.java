package es.unizar.tmdad.controller;

import es.unizar.tmdad.controller.exception.*;
import es.unizar.tmdad.dto.ConversationDto;
import es.unizar.tmdad.dto.MessageDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

public interface UserController {

    @PostMapping("/{name}/message")
    void sendNewTextMessage(@PathVariable("name") String userName, @RequestBody MessageDto msg) throws UserNotFoundException;

    void sendNewFileMessage(String userId, String sender, MultipartFile file) throws UserNotFoundException;

    ConversationDto getConversationsForUser(String userId) throws UserNotFoundException;

    @GetMapping("/{mail}/conversation/global")
    void getLastGlobalMessages(@PathVariable("mail") String user1) throws UserNotFoundException;

    @GetMapping("/{mail}/conversation/{other}")
    void getLastMessagesInPrivateChat(@PathVariable("mail") String user1, @PathVariable("other") String user2) throws UserNotFoundException;
}
