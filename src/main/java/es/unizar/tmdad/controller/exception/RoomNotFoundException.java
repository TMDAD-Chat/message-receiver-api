package es.unizar.tmdad.controller.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoomNotFoundException extends Exception{

    private final Long roomId;

}
