package es.unizar.tmdad.controller.impl;

import es.unizar.tmdad.adt.message.*;
import es.unizar.tmdad.controller.*;
import es.unizar.tmdad.controller.exception.*;
import es.unizar.tmdad.dto.*;
import es.unizar.tmdad.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserControllerImpl implements UserController {

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
    public void sendNewTextMessage(@PathVariable("name") String userName, @RequestBody MessageDto msg) throws UserNotFoundException {
        if(!userService.existsUser(userName)){
            throw new UserNotFoundException(userName);
        }

        if(!userService.existsUser(msg.getSender())){
            throw new UserNotFoundException(msg.getSender());
        }

        Message eventMessage = Message.builder()
                .messageType(MessageType.TEXT)
                .content(msg.getContent())
                .sender(msg.getSender())
                .build();

        messageService.sendMessage(eventMessage, userName, RecipientType.USER);
    }

    @Override
    @PostMapping("/{name}/file")
    public void sendNewFileMessage(@PathVariable("name") String userName, @RequestParam("sender") String sender, @RequestParam("file") MultipartFile file) throws UserNotFoundException {
        if(!userService.existsUser(userName)){
            throw new UserNotFoundException(userName);
        }

        if(!userService.existsUser(sender)){
            throw new UserNotFoundException(sender);
        }
        String fileName = this.fileService.store(file, sender).block();

        Message eventMessage = Message.builder()
                .messageType(MessageType.FILE)
                .content(fileName)
                .sender(sender)
                .build();

        messageService.sendMessage(eventMessage, userName, RecipientType.USER);
    }

    @Override
    @GetMapping("/{mail}/conversations")
    public ConversationDto getConversationsForUser(@PathVariable("mail") String userMail) throws UserNotFoundException {
        if(!userService.existsUser(userMail)){
            log.info("User not found: {}", userMail);
            throw new UserNotFoundException(userMail);
        }

        return messageService.getConversations(userMail);
    }

    @Override
    @GetMapping("/{mail}/conversation/{other}")
    public void getLastMessagesInPrivateChat(@PathVariable("mail") String user1, @PathVariable("other") String user2) throws UserNotFoundException {
        if(!userService.existsUser(user1)){
            throw new UserNotFoundException(user1);
        }
        if(!userService.existsUser(user2)){
            throw new UserNotFoundException(user2);
        }

        messageService.getLastMessagesInPrivateChat(user1, user2);
    }
}
