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

    private final String MY_USER_NAME_TEMP = "test"; //REMOVE LATER

    private final MessageService messageService;
    private final UserService userService;

    public UserControllerImpl(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @Override
    @PostMapping("/{name}/message")
    public void sendNewTextMessage(@PathVariable("name") String userName, MessageDto msg) throws UserNotFoundException {
        if(!userService.existsUser(userName)){
            throw new UserNotFoundException(userName);
        }

        Message eventMessage = Message.builder()
                .messageType(MessageType.TEXT)
                .content(msg.getContent())
                .sender(MY_USER_NAME_TEMP)
                .build();

        messageService.sendMessage(eventMessage, userName, RecipientType.USER);
    }

    @Override
    @PostMapping("/{name}/file")
    public void sendNewFileMessage(@PathVariable("name") String userName, MessageDto msg) throws UserNotFoundException {
        if(!userService.existsUser(userName)){
            throw new UserNotFoundException(userName);
        }

        Message eventMessage = Message.builder()
                .messageType(MessageType.FILE)
                .content("RANDOM_HASH_PENDING")
                .sender(MY_USER_NAME_TEMP)
                .build();

        messageService.sendMessage(eventMessage, userName, RecipientType.USER);
    }
}
