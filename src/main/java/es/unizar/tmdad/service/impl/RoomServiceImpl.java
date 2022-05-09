package es.unizar.tmdad.service.impl;

import es.unizar.tmdad.repository.RoomRepository;
import es.unizar.tmdad.service.RoomService;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public boolean existsRoom(Long roomId) {
        return roomRepository.existsById(roomId);
    }
}
