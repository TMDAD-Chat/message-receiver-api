package es.unizar.tmdad.repository;

import es.unizar.tmdad.repository.entity.ConversationFakeEntity;
import es.unizar.tmdad.repository.entity.MessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<MessageEntity, String> {

    List<MessageEntity> findAllByRecipientAndRecipientTypeAndCreationTimestampIsAfterOrderByCreationTimestampAsc(String recipient, String recipientType, Date creationTimestamp);
    List<MessageEntity> findAllByRecipientAndRecipientTypeAndCreationTimestampIsAfterOrderByCreationTimestampAsc(String recipient, String recipientType, Date creationTimestamp, Pageable page);

    @Query("SELECT new es.unizar.tmdad.repository.entity.ConversationFakeEntity(sender) FROM messages WHERE recipient = ?1 AND recipientType <> 'GLOBAL' GROUP BY sender")
    List<ConversationFakeEntity> findConversationsOfUser(String sender);

    @Query("FROM messages WHERE (sender = ?1 and recipient = ?2) or (recipient = ?1 and sender = ?2) ORDER BY creationTimestamp DESC")
    List<MessageEntity> findAllMessagesSentBetween(String user1, String user2, Pageable page);

    List<MessageEntity> findMessageEntitiesByRecipientTypeOrderByCreationTimestampDesc(String recipientType, Pageable ofSize);
}
