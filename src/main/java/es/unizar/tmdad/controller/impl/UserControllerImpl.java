package es.unizar.tmdad.controller.impl;

import es.unizar.tmdad.adt.message.*;
import es.unizar.tmdad.controller.*;
import es.unizar.tmdad.controller.exception.*;
import es.unizar.tmdad.dto.*;
import es.unizar.tmdad.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

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
    public void sendNewTextMessage(@PathVariable("name") String userName, @RequestBody MessageDto msg, @RequestHeader("X-Auth-User") String authEmail) throws UserNotFoundException, UnauthorizedException, InvalidMessageException {

        if(!Objects.equals(authEmail, msg.getSender())){
            throw new UnauthorizedException("Auth email does not match sender email.");
        }

        if(!userService.existsUser(userName)){
            throw new UserNotFoundException(userName);
        }

        if(!userService.existsUser(msg.getSender())){
            throw new UserNotFoundException(msg.getSender());
        }

        if(Objects.isNull(msg.getContent()) || msg.getContent().isEmpty() || msg.getContent().length() > 500){
            throw new InvalidMessageException(msg.getContent());
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
    public void sendNewFileMessage(@PathVariable("name") String userName, @RequestHeader("X-Auth-User") String sender, @RequestHeader("X-Auth-Firebase") String token, @RequestParam("file") MultipartFile file) throws UserNotFoundException {

        if(!userService.existsUser(userName)){
            throw new UserNotFoundException(userName);
        }

        if(!userService.existsUser(sender)){
            throw new UserNotFoundException(sender);
        }
        String fileName = this.fileService.store(file, sender, token, sender).block();

        Message eventMessage = Message.builder()
                .messageType(MessageType.FILE)
                .content(fileName)
                .sender(sender)
                .build();

        messageService.sendMessage(eventMessage, userName, RecipientType.USER);
    }

    @Override
    @GetMapping("/{mail}/conversations")
    public ConversationDto getConversationsForUser(@PathVariable("mail") String userMail, @RequestHeader("X-Auth-User") String authEmail) throws UserNotFoundException, UnauthorizedException {

        if(!Objects.equals(authEmail, userMail)){
            throw new UnauthorizedException("Auth email does not match sender email.");
        }

        if(!userService.existsUser(userMail)){
            log.info("User not found: {}", userMail);
            throw new UserNotFoundException(userMail);
        }

        return messageService.getConversations(userMail);
    }

    @Override
    @GetMapping("/{mail}/conversation/global")
    public void getLastGlobalMessages(@PathVariable("mail") String user1, @RequestHeader("X-Auth-User") String authEmail) throws UserNotFoundException, UnauthorizedException {
        if(!Objects.equals(authEmail, user1)){
            throw new UnauthorizedException("Auth email does not match sender email.");
        }

        if(!userService.existsUser(user1)){
            throw new UserNotFoundException(user1);
        }

        messageService.getLastGlobalMessagesInPrivateChat(user1);
    }

    @Override
    @GetMapping("/{mail}/conversation/{other}")
    public void getLastMessagesInPrivateChat(@PathVariable("mail") String user1, @PathVariable("other") String user2, @RequestHeader("X-Auth-User") String authEmail) throws UserNotFoundException, UnauthorizedException {
        if(!Objects.equals(authEmail, user1)){
            throw new UnauthorizedException("Auth email does not match sender email.");
        }

        if(!userService.existsUser(user1)){
            throw new UserNotFoundException(user1);
        }
        if(!userService.existsUser(user2)){
            throw new UserNotFoundException(user2);
        }

        messageService.getLastMessagesInPrivateChat(user1, user2);
    }
}
