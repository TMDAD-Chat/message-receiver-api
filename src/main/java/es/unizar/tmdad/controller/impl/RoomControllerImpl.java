package es.unizar.tmdad.controller.impl;

import es.unizar.tmdad.adt.message.Message;
import es.unizar.tmdad.adt.message.MessageType;
import es.unizar.tmdad.adt.message.RecipientType;
import es.unizar.tmdad.controller.RoomController;
import es.unizar.tmdad.controller.exception.RoomNotFoundException;
import es.unizar.tmdad.controller.exception.UserNotFoundException;
import es.unizar.tmdad.dto.MessageDto;
import es.unizar.tmdad.service.FileService;
import es.unizar.tmdad.service.MessageService;
import es.unizar.tmdad.service.RoomService;
import es.unizar.tmdad.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/room")
public class RoomControllerImpl implements RoomController {

    private final MessageService messageService;
    private final FileService fileService;
    private final UserService userService;
    private final RoomService roomService;

    public RoomControllerImpl(MessageService messageService, FileService fileService, UserService userService, RoomService roomService) {
        this.messageService = messageService;
        this.fileService = fileService;
        this.userService = userService;
        this.roomService = roomService;
    }

    @Override
    @PostMapping("/{id}/message")
    public void sendNewTextMessage(@PathVariable("id") Long roomId, @RequestBody MessageDto msg) throws UserNotFoundException, RoomNotFoundException {
        if(!userService.existsUser(msg.getSender())){
            throw new UserNotFoundException(msg.getSender());
        }
        if(!roomService.existsRoom(roomId)){
            throw new RoomNotFoundException(roomId);
        }
        Message eventMessage = Message.builder()
                .messageType(MessageType.TEXT)
                .content(msg.getContent())
                .sender(msg.getSender())
                .build();

        messageService.sendMessage(eventMessage, String.valueOf(roomId), RecipientType.ROOM);
    }

    @Override
    @PostMapping("/{id}/file")
    public void sendNewFileMessage(@PathVariable("id") Long roomId, @RequestParam("sender") String sender, @RequestParam("file") MultipartFile file) throws UserNotFoundException {
        if(!userService.existsUser(sender)){
            throw new UserNotFoundException(sender);
        }

        String fileName = this.fileService.store(file, String.valueOf(roomId)).block();
        Message eventMessage = Message.builder()
                .messageType(MessageType.FILE)
                .content(fileName)
                .sender(sender)
                .build();

        messageService.sendMessage(eventMessage, String.valueOf(roomId), RecipientType.ROOM);
    }

    @Override
    @GetMapping("/{roomId}/conversation/{mail}")
    public void getLastMessagesInRoomFor(@PathVariable("roomId") Long room, @PathVariable("mail") String user) throws UserNotFoundException, RoomNotFoundException {
        if(!userService.existsUser(user)){
            throw new UserNotFoundException(user);
        }
        if(!roomService.existsRoom(room)){
            throw new RoomNotFoundException(room);
        }

        messageService.getLastMessagesInRoom(room, user);
    }
}
