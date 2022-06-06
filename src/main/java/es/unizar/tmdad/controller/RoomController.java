package es.unizar.tmdad.controller;

import es.unizar.tmdad.controller.exception.InvalidMessageException;
import es.unizar.tmdad.controller.exception.RoomNotFoundException;
import es.unizar.tmdad.controller.exception.UnauthorizedException;
import es.unizar.tmdad.controller.exception.UserNotFoundException;
import es.unizar.tmdad.controller.exception.UserNotInTheRoomException;
import es.unizar.tmdad.dto.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface RoomController {
    @Operation(description = "Send a text message to a room",
            parameters = {
                    @Parameter(name = "X-Auth-Firebase", required = true, in = ParameterIn.HEADER, description = "Authentication token (JWT)"),
                    @Parameter(name = "X-Auth-User", required = true, in = ParameterIn.HEADER, description = "Email of the user the X-Auth-Firebase token belongs to.")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message was sent and will be received by the clients in a few seconds.")
    })
    @PostMapping("/{id}/message")
    void sendNewTextMessage(@Parameter(description = "Room identifier") @PathVariable("id") Long roomId,
                            @RequestBody MessageDto msg,
                            @RequestHeader("X-Auth-User") String authEmail) throws UserNotFoundException, RoomNotFoundException, UserNotInTheRoomException, UnauthorizedException, InvalidMessageException;

    @Operation(description = "Send a file message to a room",
            parameters = {
                    @Parameter(name = "X-Auth-Firebase", required = true, in = ParameterIn.HEADER, description = "Authentication token (JWT)"),
                    @Parameter(name = "X-Auth-User", required = true, in = ParameterIn.HEADER, description = "Email of the user the X-Auth-Firebase token belongs to.")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message was sent and will be received by the clients in a few seconds.")
    })
    @PostMapping("/{id}/file")
    void sendNewFileMessage(@Parameter(description = "Room identifier") @PathVariable("id") Long roomId,
                            @RequestHeader("X-Auth-User") String sender,
                            @RequestHeader("X-Auth-Firebase") String token,
                            @Parameter(description = "File to be sent to all users in this room") @RequestParam("file") MultipartFile file) throws UserNotFoundException,UserNotInTheRoomException;

    @Operation(description = "Get the last 30 messages of this room. If everything is OK, the messages will be received via the usual SSE endpoint, this method won't retrieve any message.",
            parameters = {
                    @Parameter(name = "X-Auth-Firebase", required = true, in = ParameterIn.HEADER, description = "Authentication token (JWT)"),
                    @Parameter(name = "X-Auth-User", required = true, in = ParameterIn.HEADER, description = "Email of the user the X-Auth-Firebase token belongs to.")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message was sent and will be received by the clients in a few seconds.")
    })
    @GetMapping("/{roomId}/conversation")
    void getLastMessagesInRoomFor(@Parameter(description = "Room identifier") @PathVariable("roomId") Long room,
                                  @Parameter(description = "Email of the user who belongs to this room") String user) throws UserNotFoundException, RoomNotFoundException,UserNotInTheRoomException;
}
