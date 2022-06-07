package es.unizar.tmdad.controller.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InvalidMessageException extends Exception{

    private final String message;

}
