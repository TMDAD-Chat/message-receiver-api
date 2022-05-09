package es.unizar.tmdad.service;

import es.unizar.tmdad.adt.message.Message;
import es.unizar.tmdad.adt.message.MessageList;
import es.unizar.tmdad.adt.message.RecipientType;
import es.unizar.tmdad.dto.ConversationDto;

public interface MessageService {

    void sendMessage(Message message, String recipient, RecipientType recipientType);
    void sendMessages(MessageList message);
    void sendMessages(MessageList message, boolean areOldMessages);

    ConversationDto getConversations(String userMail);

    void getLastMessagesInPrivateChat(String user1, String user2);

    void getLastGlobalMessagesInPrivateChat(String user1);

    void getLastMessagesInRoom(Long room, String user);
}
