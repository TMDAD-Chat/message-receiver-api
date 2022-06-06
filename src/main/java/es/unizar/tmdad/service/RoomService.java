package es.unizar.tmdad.service;

public interface RoomService {

    boolean existsRoom(Long roomId);
    boolean isUserInTheRoom(String userId, Long roomId);
}
