package es.unizar.tmdad.controller.exception;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserNotFoundException extends Exception{

    private final String userId;

}
