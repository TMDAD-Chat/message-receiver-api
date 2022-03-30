package es.unizar.tmdad.mapper;

import es.unizar.tmdad.adt.message.Message;
import es.unizar.tmdad.listener.repository.entity.MessageEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageEntity mapMessage(Message msg);
    Message mapMessage(MessageEntity msg);

}
