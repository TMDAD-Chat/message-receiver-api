package es.unizar.tmdad.adt.message;

import lombok.Data;

import java.util.Date;

@Data
public class MessageRequest {

    private String requestId;
    private String recipient;
    private RecipientType recipientType;
    private Date since;

}
