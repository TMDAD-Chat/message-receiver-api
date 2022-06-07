package es.unizar.tmdad.controller;

import es.unizar.tmdad.controller.exception.InvalidMessageException;
import es.unizar.tmdad.controller.exception.UnauthorizedException;
import es.unizar.tmdad.controller.exception.UserNotFoundException;
import es.unizar.tmdad.dto.ConversationDto;
import es.unizar.tmdad.dto.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface UserController {

    @Operation(description = "Send a text message to another user",
            parameters = {
                    @Parameter(name = "X-Auth-Firebase", required = true, in = ParameterIn.HEADER, description = "Authentication token (JWT)"),
                    @Parameter(name = "X-Auth-User", required = true, in = ParameterIn.HEADER, description = "Email of the user the X-Auth-Firebase token belongs to.")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message was sent and will be received by the client in a few seconds.")
    })
    void sendNewTextMessage(@Parameter(description = "Message recipient's email") String email,
                            MessageDto msg,
                            @Parameter(description = "Message sender's email") String authEmail) throws UserNotFoundException, UnauthorizedException, InvalidMessageException;

    @Operation(description = "Send a file message to another user",
            parameters = {
                    @Parameter(name = "X-Auth-Firebase", required = true, in = ParameterIn.HEADER, description = "Authentication token (JWT)"),
                    @Parameter(name = "X-Auth-User", required = true, in = ParameterIn.HEADER, description = "Email of the user the X-Auth-Firebase token belongs to.")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message was sent and will be received by the client in a few seconds.")
    })
    @PostMapping("/{name}/file")
    void sendNewFileMessage(@Parameter(description = "Message recipient's email") @PathVariable("name") String email,
                            @RequestHeader("X-Auth-User") String sender, @RequestHeader("X-Auth-Firebase") String token,
                            @Parameter(description = "Message file") @RequestParam("file") MultipartFile file) throws UserNotFoundException;

    @Operation(description = "Get all the users a specific user has interacted with.",
            parameters = {
                    @Parameter(name = "X-Auth-Firebase", required = true, in = ParameterIn.HEADER, description = "Authentication token (JWT)"),
                    @Parameter(name = "X-Auth-User", required = true, in = ParameterIn.HEADER, description = "Email of the user the X-Auth-Firebase token belongs to.")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request was fulfilled and messages will be received by the client in a few seconds.")
    })
    ConversationDto getConversationsForUser(@Parameter(description = "Email of the user to retrieve messages, must match X-Auth-User header") String userMail, String authEmail) throws UserNotFoundException, UnauthorizedException;

    @Operation(description = "Get the last 30 messages of this private chat. If everything is OK, the messages will be received via the usual SSE endpoint, this method won't retrieve any message.",
            parameters = {
                    @Parameter(name = "X-Auth-Firebase", required = true, in = ParameterIn.HEADER, description = "Authentication token (JWT)"),
                    @Parameter(name = "X-Auth-User", required = true, in = ParameterIn.HEADER, description = "Email of the user the X-Auth-Firebase token belongs to.")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request was fulfilled and messages will be received by the client in a few seconds.")
    })
    void getLastMessagesInPrivateChat(@Parameter(description = "Email of the user to retrieve messages of, must match X-Auth-User header") String user1,
                                      @Parameter(description = "Email of the other user in the conversation") String user2, String authEmail) throws UserNotFoundException, UnauthorizedException;

    @Operation(description = "Get the last 30 global messages. If everything is OK, the messages will be received via the usual SSE endpoint, this method won't retrieve any message.",
            parameters = {
                    @Parameter(name = "X-Auth-Firebase", required = true, in = ParameterIn.HEADER, description = "Authentication token (JWT)"),
                    @Parameter(name = "X-Auth-User", required = true, in = ParameterIn.HEADER, description = "Email of the user the X-Auth-Firebase token belongs to.")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request was fulfilled and messages will be received by the client in a few seconds.")
    })
    void getLastGlobalMessages(@Parameter(description = "Email of the user to retrieve messages of, must match X-Auth-User header") String user1, String authEmail) throws UserNotFoundException, UnauthorizedException;
}
