package es.unizar.tmdad.controller;

import es.unizar.tmdad.controller.exception.*;
import es.unizar.tmdad.dto.MessageDto;

public interface UserController {

    void sendNewTextMessage(String userId, MessageDto msg) throws UserNotFoundException;
    void sendNewFileMessage(String userId, MessageDto msg) throws UserNotFoundException;

}
