package es.unizar.tmdad.controller.impl;

import es.unizar.tmdad.adt.message.*;
import es.unizar.tmdad.controller.*;
import es.unizar.tmdad.controller.exception.*;
import es.unizar.tmdad.dto.*;
import es.unizar.tmdad.service.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserControllerImpl implements UserController {

    private final String MY_USER_NAME_TEMP = "test"; //REMOVE LATER

    private final MessageService messageService;
    private final UserService userService;
    private final FileService fileService;

    public UserControllerImpl(MessageService messageService, UserService userService, FileService fileService) {
        this.messageService = messageService;
        this.userService = userService;
        this.fileService = fileService;
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
    public void sendNewFileMessage(@PathVariable("name") String userName, @RequestParam("file") MultipartFile file) throws UserNotFoundException {
        if(!userService.existsUser(userName)){
            throw new UserNotFoundException(userName);
        }
        String fileName = this.fileService.store(file, userName).block();

        Message eventMessage = Message.builder()
                .messageType(MessageType.FILE)
                .content(fileName)
                .sender(MY_USER_NAME_TEMP)
                .build();

        messageService.sendMessage(eventMessage, userName, RecipientType.USER);
    }
}
