package es.unizar.tmdad.controller;

import es.unizar.tmdad.controller.exception.InvalidMessageException;
import es.unizar.tmdad.controller.exception.UnauthorizedException;
import es.unizar.tmdad.controller.exception.UserNotFoundException;
import es.unizar.tmdad.dto.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface MessageController {

    @Operation(description = "Send a message to all users, either connected or not",
            parameters = {
                    @Parameter(name = "X-Auth-Firebase", required = true, in = ParameterIn.HEADER, description = "Authentication token (JWT)"),
                    @Parameter(name = "X-Auth-User", required = true, in = ParameterIn.HEADER, description = "Email of the user the X-Auth-Firebase token belongs to.")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message was sent and will be received by the clients in a few seconds.")
    })
    void sendGlobalMessage(@Parameter(description = "Message to be sent") MessageDto msg, @Parameter(description = "User who sent the message") String authEmail) throws UnauthorizedException, UserNotFoundException, InvalidMessageException;
}
