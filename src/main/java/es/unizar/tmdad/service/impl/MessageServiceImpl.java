package es.unizar.tmdad.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.tmdad.adt.message.Message;
import es.unizar.tmdad.mapper.MessageMapper;
import es.unizar.tmdad.listener.repository.MessageRepository;
import es.unizar.tmdad.listener.repository.entity.MessageEntity;
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

    public MessageServiceImpl(MessageRepository repository, MessageMapper mapper, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @Override
    public void sendMessage(Message message) {
        //SAVE TO DB
        MessageEntity savedEntity = repository.save(mapper.mapMessage(message));

        //SEND TO RABBIT
        message = mapper.mapMessage(savedEntity);
        rabbitTemplate.convertAndSend(exchangeName, "", objectMapper.writeValueAsString(message));
    }
}
