package es.unizar.tmdad.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "Object representing a collection of conversations of a user")
public class ConversationDto {

    @Schema(description = "Collection of conversations")
    private List<Conversation> conversations;

    @Data
    @Builder
    public static class Conversation {
        @Schema(description = "Other user in the conversation", example = "test2@whoishim.me")
        private String sender;
    }

}
