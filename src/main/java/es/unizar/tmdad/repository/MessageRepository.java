package es.unizar.tmdad.repository;

import es.unizar.tmdad.repository.entity.MessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<MessageEntity, String> {

    List<MessageEntity> findAllByRecipientAndRecipientTypeAndCreationTimestampIsAfterOrderByCreationTimestampAsc(String recipient, String recipientType, Date creationTimestamp);
    List<MessageEntity> findAllByRecipientAndRecipientTypeAndCreationTimestampIsAfterOrderByCreationTimestampAsc(String recipient, String recipientType, Date creationTimestamp, Pageable page);

}
