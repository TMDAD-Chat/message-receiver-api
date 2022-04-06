package es.unizar.tmdad.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.tmdad.adt.message.MessageList;
import es.unizar.tmdad.adt.message.MessageRequest;
import es.unizar.tmdad.adt.message.RecipientType;
import es.unizar.tmdad.mapper.MessageMapper;
import es.unizar.tmdad.repository.MessageRepository;
import es.unizar.tmdad.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class OldMessageListener {

    private final ObjectMapper objectMapper;
    private final MessageRepository messageRepository;
    private final MessageService messageService;
    private final MessageMapper messageMapper;

    public OldMessageListener(ObjectMapper objectMapper, MessageRepository messageRepository, MessageService messageService, MessageMapper messageMapper) {
        this.objectMapper = objectMapper;
        this.messageRepository = messageRepository;
        this.messageService = messageService;
        this.messageMapper = messageMapper;
    }

    public void apply(String input) throws JsonProcessingException {
        MessageRequest msg = objectMapper.readValue(input, MessageRequest.class);
        log.info("Processing old message event {}.", msg);

        String recipient = msg.getRecipient();
        if(Objects.equals(msg.getRecipientType(), RecipientType.GLOBAL)){
            recipient = null;
        }

        var messageList = this.messageRepository.findAllByRecipientAndRecipientTypeAndCreationTimestampIsAfterOrderByCreationTimestampAsc(
                recipient,
                msg.getRecipientType().name(),
                msg.getSince());
                //Pageable.ofSize(3));


        MessageList outputMessageList = MessageList.builder()
                .requestId(msg.getRequestId())
                .recipient(msg.getRecipient())
                .recipientType(msg.getRecipientType())
                .messages(this.messageMapper.mapMessageEntities(messageList))
                .build();

        log.info("Retrieved {} old messages for {}.", messageList.size(), msg.getRequestId());

        //We still send the event even if no messages are retrieved from DB
        this.messageService.sendMessages(outputMessageList, true);
    }
}
