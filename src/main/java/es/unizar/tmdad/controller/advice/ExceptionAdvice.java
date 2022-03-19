package es.unizar.tmdad.controller.advice;

import es.unizar.tmdad.controller.exception.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<Void> handleUserNotFound(UserNotFoundException ignoredException){
        return ResponseEntity.notFound().build();
    }

}
