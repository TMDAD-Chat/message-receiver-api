package es.unizar.tmdad.controller.advice;

import es.unizar.tmdad.controller.exception.InvalidMessageException;
import es.unizar.tmdad.controller.exception.RoomNotFoundException;
import es.unizar.tmdad.controller.exception.UnauthorizedException;
import es.unizar.tmdad.controller.exception.UserNotFoundException;
import es.unizar.tmdad.controller.exception.UserNotInTheRoomException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(value = {UserNotFoundException.class})
    @ApiResponse(description = "Entity could not be found, or user is not in this room", responseCode = "404")
    public ResponseEntity<Void> handleUserNotFound(UserNotFoundException ignoredException){
        return ResponseEntity.notFound().build();
    }
    @ExceptionHandler(value = {RoomNotFoundException.class})
    @ApiResponse(description = "Entity could not be found, or user is not in this room", responseCode = "404")
    public ResponseEntity<Void> handleRoomNotFound(RoomNotFoundException ignoredException){
        return ResponseEntity.notFound().build();
    }
    @ExceptionHandler(value = {UserNotInTheRoomException.class})
    @ApiResponse(description = "Entity could not be found, or user is not in this room", responseCode = "404")
    public ResponseEntity<Void> handleRoomNotFound(UserNotInTheRoomException ignoredException){
        return ResponseEntity.notFound().build();
    }
    @ExceptionHandler(value = {InvalidMessageException.class})
    @ApiResponse(description = "Invalid message encountered", responseCode = "400")
    public ResponseEntity<Void> handleInvalidMessage(InvalidMessageException ignoredException){
        return ResponseEntity.badRequest().build();
    }
    @ExceptionHandler(value = {UnauthorizedException.class})
    @ApiResponse(description = "User is not allowed to call this endpoint, check they have the proper permissions.", responseCode = "401")
    public ResponseEntity<Void> handleRoomNotFound(UnauthorizedException ignoredException){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
