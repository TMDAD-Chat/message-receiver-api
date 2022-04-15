package es.unizar.tmdad.controller.impl;

import es.unizar.tmdad.adt.message.Message;
import es.unizar.tmdad.adt.message.MessageType;
import es.unizar.tmdad.adt.message.RecipientType;
import es.unizar.tmdad.controller.RoomController;
import es.unizar.tmdad.dto.MessageDto;
import es.unizar.tmdad.service.FileService;
import es.unizar.tmdad.service.MessageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/room")
public class RoomControllerImpl implements RoomController {

    private final String MY_USER_ID_TEMP = "1"; //REMOVE LATER

    private final MessageService messageService;
    private final FileService fileService;

    public RoomControllerImpl(MessageService messageService, FileService fileService) {
        this.messageService = messageService;
        this.fileService = fileService;
    }

    @Override
    @PostMapping("/{id}/message")
    public void sendNewTextMessage(@PathVariable("id") String userId, MessageDto msg) {
        Message eventMessage = Message.builder()
                .messageType(MessageType.TEXT)
                .content(msg.getContent())
                .sender(MY_USER_ID_TEMP)
                .build();

        messageService.sendMessage(eventMessage, userId, RecipientType.ROOM);
    }

    @Override
    @PostMapping("/{id}/file")
    public void sendNewFileMessage(@PathVariable("id") String userId, @RequestParam("file") MultipartFile file) {
        String fileName = this.fileService.store(file, userId).block();
        Message eventMessage = Message.builder()
                .messageType(MessageType.FILE)
                .content(fileName)
                .sender(MY_USER_ID_TEMP)
                .build();

        messageService.sendMessage(eventMessage, userId, RecipientType.USER);
    }
}
