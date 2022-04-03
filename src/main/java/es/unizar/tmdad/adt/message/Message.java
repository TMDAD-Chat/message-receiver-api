package es.unizar.tmdad.adt.message;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Message {

    private MessageType messageType;
    private String content;
    private String sender;
    private Date creationTimestamp;

}
