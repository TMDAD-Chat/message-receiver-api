package es.unizar.tmdad.controller.impl;

import es.unizar.tmdad.adt.message.Message;
import es.unizar.tmdad.adt.message.MessageType;
import es.unizar.tmdad.adt.message.RecipientType;
import es.unizar.tmdad.controller.MessageController;
import es.unizar.tmdad.dto.MessageDto;
import es.unizar.tmdad.service.MessageService;
import es.unizar.tmdad.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public void sendGlobalMessage(MessageDto msg) {

        Message eventMessage = Message.builder()
                .messageType(MessageType.TEXT)
                .content(msg.getContent())
                .sender(MY_USER_NAME_TEMP)
                .build();

        messageService.sendMessage(eventMessage, null, RecipientType.GLOBAL);
    }

}
