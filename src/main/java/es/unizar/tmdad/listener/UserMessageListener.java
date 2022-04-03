package es.unizar.tmdad.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.tmdad.adt.user.UserInEvent;
import es.unizar.tmdad.repository.RoomRepository;
import es.unizar.tmdad.repository.UserRepository;
import es.unizar.tmdad.repository.entity.RoomEntity;
import es.unizar.tmdad.repository.entity.UserEntity;
import es.unizar.tmdad.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@Slf4j
public class UserMessageListener {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final UserService userService;


    @Value("${chat.exchanges.output:message-pcs}")
    private String topicExchangeName;

    public UserMessageListener(ObjectMapper objectMapper, UserRepository userRepository,RoomRepository roomRepository, UserService userService) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.roomRepository=roomRepository;
        this.userService = userService;
    }

    public void apply(String input) throws JsonProcessingException {
        UserInEvent msg = objectMapper.readValue(input, UserInEvent.class);
        log.info("Processing user event {}.", msg);

        switch (msg.getEvent()){

            case ADD_USER:
                UserEntity addedUser = new UserEntity(msg.getSubject(), new HashSet<>());
                userRepository.save(addedUser);
                break;
            case DELETE_USER:
                userRepository.deleteById(msg.getSubject());
                break;
            case ADD_ROOM:
                RoomEntity roomEntity = RoomEntity.builder().id(Long.parseLong(msg.getSubject())).build();
                roomRepository.save(roomEntity);
                break;
            case DELETE_ROOM:
                roomRepository.deleteById(Long.parseLong(msg.getSubject()));
                break;
            case ADD_USER_TO_ROOM:
                if(userService.existsUser(msg.getSubject())){
                    RoomEntity addedUserToRoom= RoomEntity.builder().id(Long.parseLong(msg.getSubject())).build();
                    addedUserToRoom.getUsers().add(UserEntity.builder().name(msg.getSubject()).build());
                    roomRepository.save(addedUserToRoom);
                }
                break;
            case REMOVE_USER_FROM_ROOM:
                if(userService.existsUser(msg.getSubject())){
                    RoomEntity removedUserToRoom = RoomEntity.builder().id(Long.parseLong(msg.getSubject())).build();
                    removedUserToRoom.getUsers().remove(UserEntity.builder().name(msg.getSubject()).build());
                    roomRepository.save(removedUserToRoom);
                }
                break;
        }
    }
}
