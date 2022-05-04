package es.unizar.tmdad.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.tmdad.adt.message.Message;
import es.unizar.tmdad.adt.message.MessageList;
import es.unizar.tmdad.adt.message.RecipientType;
import es.unizar.tmdad.dto.ConversationDto;
import es.unizar.tmdad.mapper.MessageMapper;
import es.unizar.tmdad.repository.MessageRepository;
import es.unizar.tmdad.repository.entity.MessageEntity;
import es.unizar.tmdad.service.CounterService;
import es.unizar.tmdad.service.MessageService;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;
    private final MessageMapper mapper;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private final CounterService counterService;

    @Value("${chat.exchanges.output}")
    private String exchangeName;

    @Value("${chat.exchanges.reprocess-messages}")
    private String oldMessagesExchangeName;

    public MessageServiceImpl(MessageRepository repository, MessageMapper mapper, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, CounterService counterService) {
        this.repository = repository;
        this.mapper = mapper;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.counterService = counterService;
    }

    @SneakyThrows
    @Override
    public void sendMessage(Message message, String recipient, RecipientType recipientType) {
        this.counterService.newMessageReceived(objectMapper.writeValueAsString(message));
        //SAVE TO DB
        MessageEntity savedEntity = repository.save(mapper.mapMessage(message, recipient, recipientType));

        //SEND TO RABBIT
        var messageList = mapper.mapMessage(savedEntity);
        String messageAsString = objectMapper.writeValueAsString(messageList);
        this.counterService.newMessageSent(messageAsString);
        rabbitTemplate.convertAndSend(exchangeName, "", messageAsString);
    }

    @SneakyThrows
    @Override
    public void sendMessages(MessageList message) {
        this.sendMessages(message, false);
    }

    @SneakyThrows
    @Override
    public void sendMessages(MessageList message, boolean areOldMessages) {
        String outputExchange = areOldMessages ? oldMessagesExchangeName : exchangeName;
        String messageAsString = objectMapper.writeValueAsString(message);
        this.counterService.newMessageSent(messageAsString);
        rabbitTemplate.convertAndSend(outputExchange, "", messageAsString);
    }

    @Override
    public ConversationDto getConversations(String userMail) {
        var messageEntityList = this.repository.findConversationsOfUser(userMail);

        return ConversationDto.builder()
                .conversations(
                        messageEntityList.stream()
                                .map(e -> ConversationDto.Conversation.builder()
                                        .sender(e.getSender())
                                        .build())
                                .collect(Collectors.toList())
                ).build();

    }

    @Override
    public void getLastMessagesInPrivateChat(String user1, String user2) {
        var messageList = this.repository.findAllMessagesSentBetween(user1, user2, Pageable.ofSize(30));

        //splittedMessages[true] are the messages sent by the user who requested the messages
        //splittedMessages[false] are the messages received by the user who requested the messages
        var splittedMessages = messageList.stream()
                .collect(Collectors.partitioningBy(messageEntity -> Objects.equals(messageEntity.getSender(), user1)));
        var messagesBySender = mapper.mapMessageEntities(splittedMessages.get(Boolean.TRUE));
        var messagesByReceiver = mapper.mapMessageEntities(splittedMessages.get(Boolean.FALSE));

        if(Objects.nonNull(messagesBySender) && !messagesBySender.isEmpty()) {
            MessageList messagesSentByUser = MessageList.builder()
                    .requestId("REQUESTED_BY_SENDER")
                    .recipient(user2)
                    .recipientType(RecipientType.USER)
                    .messages(messagesBySender)
                    .build();

            this.sendMessages(messagesSentByUser, true);
        }

        if(Objects.nonNull(messagesByReceiver) && !messagesByReceiver.isEmpty()) {
            MessageList messagesReceivedByUser = MessageList.builder()
                    .requestId("REQUESTED_BY_RECIPIENT")
                    .recipient(user1)
                    .recipientType(RecipientType.USER)
                    .messages(messagesByReceiver)
                    .build();

            this.sendMessages(messagesReceivedByUser, true);
        }
    }

    @Override
    public void getLastGlobalMessagesInPrivateChat(String user1) {
        var messageList = this.repository.findMessageEntitiesByRecipientTypeOrderByCreationTimestampDesc(RecipientType.GLOBAL.toString(), Pageable.ofSize(30));

        var globalMessages = mapper.mapMessageEntities(messageList);

        if(Objects.nonNull(globalMessages) && !globalMessages.isEmpty()) {
            MessageList messagesSentByUser = MessageList.builder()
                    .requestId("REQUESTED_BY_SENDER")
                    .recipient(user1)
                    .recipientType(RecipientType.GLOBAL)
                    .messages(globalMessages)
                    .build();

            this.sendMessages(messagesSentByUser, true);
        }
    }
}
