package es.unizar.tmdad.controller.impl;

import es.unizar.tmdad.adt.message.Message;
import es.unizar.tmdad.adt.message.MessageType;
import es.unizar.tmdad.adt.message.RecipientType;
import es.unizar.tmdad.controller.RoomController;
import es.unizar.tmdad.dto.MessageDto;
import es.unizar.tmdad.service.MessageService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/room")
public class RoomControllerImpl implements RoomController {

    private final String MY_USER_ID_TEMP = "1"; //REMOVE LATER

    private final MessageService messageService;

    public RoomControllerImpl(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    @PostMapping("/{id}/message")
    public void sendNewTextMessage(@PathVariable("id") String userId, MessageDto msg) {
        Message eventMessage = Message.builder()
                .messageType(MessageType.TEXT)
                .content(msg.getContent())
                .recipient(userId)
                .recipientType(RecipientType.ROOM)
                .sender(MY_USER_ID_TEMP)
                .build();

        messageService.sendMessage(eventMessage);
    }

    @Override
    @PostMapping("/{id}/file")
    public void sendNewFileMessage(@PathVariable("id") String userId, MessageDto msg) {
        Message eventMessage = Message.builder()
                .messageType(MessageType.FILE)
                .content("RANDOM_HASH_PENDING")
                .recipient(userId)
                .recipientType(RecipientType.ROOM)
                .sender(MY_USER_ID_TEMP)
                .build();

        messageService.sendMessage(eventMessage);
    }
}
