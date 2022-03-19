package es.unizar.tmdad.controller.impl;

import es.unizar.tmdad.adt.message.*;
import es.unizar.tmdad.controller.*;
import es.unizar.tmdad.controller.exception.*;
import es.unizar.tmdad.dto.*;
import es.unizar.tmdad.service.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserControllerImpl implements UserController {

    private final String MY_USER_ID_TEMP = "1"; //REMOVE LATER

    private final MessageService messageService;
    private final UserService userService;

    public UserControllerImpl(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @Override
    @PostMapping("/{id}/message")
    public void sendNewTextMessage(@PathVariable("id") String userId, MessageDto msg) throws UserNotFoundException {
        if(!userService.existsUser(userId)){
            throw new UserNotFoundException(userId);
        }

        Message eventMessage = Message.builder()
                .messageType(MessageType.TEXT)
                .content(msg.getContent())
                .recipient(userId)
                .recipientType(RecipientType.USER)
                .sender(MY_USER_ID_TEMP)
                .build();

        messageService.sendMessage(eventMessage);
    }

    @Override
    @PostMapping("/{id}/file")
    public void sendNewFileMessage(@PathVariable("id") String userId, MessageDto msg) throws UserNotFoundException {
        if(!userService.existsUser(userId)){
            throw new UserNotFoundException(userId);
        }

        Message eventMessage = Message.builder()
                .messageType(MessageType.FILE)
                .content("RANDOM_HASH_PENDING")
                .recipient(userId)
                .recipientType(RecipientType.USER)
                .sender(MY_USER_ID_TEMP)
                .build();

        messageService.sendMessage(eventMessage);
    }
}
