package es.unizar.tmdad.service;

import es.unizar.tmdad.adt.message.Message;
import es.unizar.tmdad.adt.message.MessageList;
import es.unizar.tmdad.adt.message.RecipientType;

public interface MessageService {

    void sendMessage(Message message, String recipient, RecipientType recipientType);
    void sendMessages(MessageList message);
    void sendMessages(MessageList message, boolean areOldMessages);

}
