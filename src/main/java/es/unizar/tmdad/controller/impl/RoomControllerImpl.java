package es.unizar.tmdad.controller.impl;

import es.unizar.tmdad.adt.message.Message;
import es.unizar.tmdad.adt.message.MessageType;
import es.unizar.tmdad.adt.message.RecipientType;
import es.unizar.tmdad.controller.RoomController;
import es.unizar.tmdad.controller.exception.InvalidMessageException;
import es.unizar.tmdad.controller.exception.RoomNotFoundException;
import es.unizar.tmdad.controller.exception.UnauthorizedException;
import es.unizar.tmdad.controller.exception.UserNotFoundException;
import es.unizar.tmdad.controller.exception.UserNotInTheRoomException;
import es.unizar.tmdad.dto.MessageDto;
import es.unizar.tmdad.service.FileService;
import es.unizar.tmdad.service.MessageService;
import es.unizar.tmdad.service.RoomService;
import es.unizar.tmdad.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

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
    @Transactional
    public void sendNewTextMessage(@PathVariable("id") Long roomId, @RequestBody MessageDto msg, @RequestHeader("X-Auth-User") String authEmail) throws UserNotFoundException, RoomNotFoundException, UserNotInTheRoomException, UnauthorizedException, InvalidMessageException {
        if(!Objects.equals(authEmail, msg.getSender())){
            throw new UnauthorizedException("Auth email does not match sender email.");
        }

        if(!userService.existsUser(msg.getSender())){
            throw new UserNotFoundException(msg.getSender());
        }

        if(!roomService.existsRoom(roomId)){
            throw new RoomNotFoundException(roomId);
        }

        if(!roomService.isUserInTheRoom(msg.getSender(), roomId)){
            throw new UserNotInTheRoomException(roomId, msg.getSender());
        }

        if(Objects.isNull(msg.getContent()) || msg.getContent().isEmpty() || msg.getContent().length() > 500){
            throw new InvalidMessageException(msg.getContent());
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
    @Transactional
    public void sendNewFileMessage(@PathVariable("id") Long roomId, @RequestHeader("X-Auth-User") String sender, @RequestHeader("X-Auth-Firebase") String token, @RequestParam("file") MultipartFile file) throws UserNotFoundException,UserNotInTheRoomException {
        if(!userService.existsUser(sender)){
            throw new UserNotFoundException(sender);
        }

        if(!roomService.isUserInTheRoom(sender, roomId)){
            throw new UserNotInTheRoomException(roomId, sender);
        }


        String fileName = this.fileService.store(file, String.valueOf(roomId), token, sender).block();
        Message eventMessage = Message.builder()
                .messageType(MessageType.FILE)
                .content(fileName)
                .sender(sender)
                .build();

        messageService.sendMessage(eventMessage, String.valueOf(roomId), RecipientType.ROOM);
    }

    @Override
    @GetMapping("/{roomId}/conversation")
    @Transactional
    public void getLastMessagesInRoomFor(@PathVariable("roomId") Long room, @RequestHeader("X-Auth-User") String user) throws UserNotFoundException, RoomNotFoundException, UserNotInTheRoomException {
        if(!userService.existsUser(user)){
            throw new UserNotFoundException(user);
        }
        if(!roomService.existsRoom(room)){
            throw new RoomNotFoundException(room);
        }

        if(!roomService.isUserInTheRoom(user, room)){
            throw new UserNotInTheRoomException(room, user);
        }

        messageService.getLastMessagesInRoom(room, user);
    }
}
