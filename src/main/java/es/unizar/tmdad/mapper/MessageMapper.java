package es.unizar.tmdad.mapper;

import es.unizar.tmdad.adt.message.Message;
import es.unizar.tmdad.adt.message.MessageList;
import es.unizar.tmdad.adt.message.RecipientType;
import es.unizar.tmdad.repository.entity.MessageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "recipient", source = "recipient")
    @Mapping(target = "recipientType", source = "recipientType")
    MessageEntity mapMessage(Message msg, String recipient, RecipientType recipientType);

    @Mapping(target = "requestId", expression = "java(\"\")")
    @Mapping(target = "recipient", source = "msg.recipient")
    @Mapping(target = "recipientType", source = "msg.recipientType")
    @Mapping(target = "messages", expression = "java(mapMessages(msg))")
    MessageList mapMessage(MessageEntity msg);

    Message mapMessageEntity(MessageEntity message);
    List<Message> mapMessageEntities(List<MessageEntity> message);

    default List<Message> mapMessages(MessageEntity message) {
        return List.of(
                this.mapMessageEntity(message)
        );
    }

}
