package es.unizar.tmdad.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Object representing a message sent by somebody")
public class MessageDto {
    @Schema(description = "Sender of the message", example = "test@whoami.com")
    private String sender;

    @Schema(description = "Actual content of the message", example = "Example message to be sent to the server... Any \"stringable\" content can be sent")
    private String content;
}
