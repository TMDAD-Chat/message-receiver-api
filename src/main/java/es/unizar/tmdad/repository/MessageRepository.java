package es.unizar.tmdad.repository;

import es.unizar.tmdad.repository.entity.MessageEntity;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<MessageEntity, String> {
}
