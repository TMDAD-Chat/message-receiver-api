package es.unizar.tmdad.controller;

import es.unizar.tmdad.dto.MessageDto;

public interface RoomController {

    void sendNewTextMessage(String groupId, MessageDto msg);
    void sendNewFileMessage(String groupId, MessageDto msg);

}
