package es.unizar.tmdad.controller.impl;

import es.unizar.tmdad.adt.message.Message;
import es.unizar.tmdad.adt.message.MessageType;
import es.unizar.tmdad.adt.message.RecipientType;
import es.unizar.tmdad.controller.MessageController;
import es.unizar.tmdad.controller.exception.InvalidMessageException;
import es.unizar.tmdad.controller.exception.UnauthorizedException;
import es.unizar.tmdad.controller.exception.UserNotFoundException;
import es.unizar.tmdad.dto.MessageDto;
import es.unizar.tmdad.service.MessageService;
import es.unizar.tmdad.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/message")
public class MessageControllerImpl implements MessageController {

    private final String MY_USER_NAME_TEMP = "test"; //REMOVE LATER

    private final MessageService messageService;
    private final UserService userService;

    public MessageControllerImpl(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @Override
    @PostMapping("/global")
    public void sendGlobalMessage(MessageDto msg, @RequestHeader("X-Auth-User") String authEmail) throws UnauthorizedException, UserNotFoundException, InvalidMessageException {
        if(!this.userService.isSuperUser(authEmail)){
            throw new UnauthorizedException(authEmail);
        }

        if(Objects.isNull(msg.getContent()) || msg.getContent().isEmpty() || msg.getContent().length() > 500){
            throw new InvalidMessageException(msg.getContent());
        }

        Message eventMessage = Message.builder()
                .messageType(MessageType.TEXT)
                .content(msg.getContent())
                .sender(MY_USER_NAME_TEMP)
                .build();

        messageService.sendMessage(eventMessage, null, RecipientType.GLOBAL);
    }

}
