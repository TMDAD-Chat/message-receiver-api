package es.unizar.tmdad.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.tmdad.adt.message.Message;
import es.unizar.tmdad.adt.message.MessageList;
import es.unizar.tmdad.adt.message.RecipientType;
import es.unizar.tmdad.mapper.MessageMapper;
import es.unizar.tmdad.repository.MessageRepository;
import es.unizar.tmdad.repository.entity.MessageEntity;
import es.unizar.tmdad.service.MessageService;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;
    private final MessageMapper mapper;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${chat.exchanges.output}")
    private String exchangeName;

    @Value("${chat.exchanges.reprocess-messages}")
    private String oldMessagesExchangeName;

    public MessageServiceImpl(MessageRepository repository, MessageMapper mapper, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @Override
    public void sendMessage(Message message, String recipient, RecipientType recipientType) {
        //SAVE TO DB
        MessageEntity savedEntity = repository.save(mapper.mapMessage(message, recipient, recipientType));

        //SEND TO RABBIT
        var messageList = mapper.mapMessage(savedEntity);
        rabbitTemplate.convertAndSend(exchangeName, "", objectMapper.writeValueAsString(messageList));
    }

    @SneakyThrows
    @Override
    public void sendMessages(MessageList message){
        this.sendMessages(message, false);
    }

    @SneakyThrows
    @Override
    public void sendMessages(MessageList message, boolean areOldMessages){
        String outputExchange = areOldMessages ? oldMessagesExchangeName : exchangeName;
        rabbitTemplate.convertAndSend(outputExchange, "", objectMapper.writeValueAsString(message));
    }
}
