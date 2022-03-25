package es.unizar.tmdad.controller;

import es.unizar.tmdad.controller.exception.UserNotFoundException;
import es.unizar.tmdad.dto.MessageDto;

public interface MessageController {

    void sendGlobalMessage(MessageDto msg) throws UserNotFoundException;

}
