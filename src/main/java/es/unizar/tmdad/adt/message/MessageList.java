package es.unizar.tmdad.adt.message;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MessageList {

    private String requestId;
    private String recipient;
    private RecipientType recipientType;
    private List<Message> messages;

}
