package es.unizar.tmdad.controller.impl;

import es.unizar.tmdad.adt.message.Message;
import es.unizar.tmdad.adt.message.MessageType;
import es.unizar.tmdad.adt.message.RecipientType;
import es.unizar.tmdad.controller.RoomController;
import es.unizar.tmdad.controller.exception.UserNotFoundException;
import es.unizar.tmdad.dto.MessageDto;
import es.unizar.tmdad.service.FileService;
import es.unizar.tmdad.service.MessageService;
import es.unizar.tmdad.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/room")
public class RoomControllerImpl implements RoomController {

    private final MessageService messageService;
    private final FileService fileService;
    private final UserService userService;

    public RoomControllerImpl(MessageService messageService, FileService fileService, UserService userService) {
        this.messageService = messageService;
        this.fileService = fileService;
        this.userService = userService;
    }

    @Override
    @PostMapping("/{id}/message")
    public void sendNewTextMessage(@PathVariable("id") String userId, @RequestParam("sender") String sender, MessageDto msg) throws UserNotFoundException {
        if(!userService.existsUser(sender)){
            throw new UserNotFoundException(sender);
        }
        Message eventMessage = Message.builder()
                .messageType(MessageType.TEXT)
                .content(msg.getContent())
                .sender(sender)
                .build();

        messageService.sendMessage(eventMessage, userId, RecipientType.ROOM);
    }

    @Override
    @PostMapping("/{id}/file")
    public void sendNewFileMessage(@PathVariable("id") String userId, @RequestParam("sender") String sender, @RequestParam("file") MultipartFile file) throws UserNotFoundException {
        if(!userService.existsUser(sender)){
            throw new UserNotFoundException(sender);
        }

        String fileName = this.fileService.store(file, userId).block();
        Message eventMessage = Message.builder()
                .messageType(MessageType.FILE)
                .content(fileName)
                .sender(sender)
                .build();

        messageService.sendMessage(eventMessage, userId, RecipientType.ROOM);
    }
}
