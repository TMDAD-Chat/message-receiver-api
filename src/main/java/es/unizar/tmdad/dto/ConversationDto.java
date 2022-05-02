package es.unizar.tmdad.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ConversationDto {

    private List<Conversation> conversations;

    @Data
    @Builder
    public static class Conversation {
        private String sender;
    }

}
