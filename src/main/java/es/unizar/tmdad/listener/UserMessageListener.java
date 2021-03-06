package es.unizar.tmdad.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.tmdad.adt.message.RecipientType;
import es.unizar.tmdad.adt.user.UserInEvent;
import es.unizar.tmdad.repository.MessageRepository;
import es.unizar.tmdad.repository.RoomRepository;
import es.unizar.tmdad.repository.UserRepository;
import es.unizar.tmdad.repository.entity.RoomEntity;
import es.unizar.tmdad.repository.entity.UserEntity;
import es.unizar.tmdad.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserMessageListener {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;
    private final UserService userService;


    @Value("${chat.exchanges.output:message-pcs}")
    private String topicExchangeName;

    public UserMessageListener(ObjectMapper objectMapper, UserRepository userRepository, MessageRepository messageRepository, RoomRepository roomRepository, UserService userService) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.roomRepository=roomRepository;
        this.userService = userService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void apply(String input) throws JsonProcessingException {
        UserInEvent msg = objectMapper.readValue(input, UserInEvent.class);
        log.info("Processing user event {}.", msg);

        switch (msg.getEvent()){

            case ADD_USER:
                UserEntity addedUser = createNewUser(msg);
                userRepository.save(addedUser);
                break;
            case DELETE_USER:
                userRepository.deleteById(msg.getSubject());
                messageRepository.deleteAllBySenderAndRecipientType(msg.getSubject(), RecipientType.USER.name());
                break;
            case UPDATE_USER_SU_FLAG:
                var updatedUser = userRepository.findById(msg.getSubject()).orElse(createNewUser(msg));
                updatedUser.setSuperuser(Boolean.getBoolean(msg.getArgument()));
                userRepository.save(updatedUser);
                break;
            case ADD_ROOM:
                RoomEntity roomEntity = RoomEntity.builder()
                    .id(Long.parseLong(msg.getSubject()))
                        .users(new HashSet<>())
                        .build();
                var ownerEntity = this.userRepository.findById(msg.getArgument());
                ownerEntity.ifPresent(owner -> {
                    roomEntity.getUsers().add(owner);
                    roomRepository.save(roomEntity);
                });
                break;
            case DELETE_ROOM:
                roomRepository.deleteById(Long.parseLong(msg.getSubject()));
                messageRepository.deleteAllByRecipientAndRecipientType(msg.getSubject(), RecipientType.ROOM.name());
                break;
            case ADD_USER_TO_ROOM:
                if(userService.existsUser(msg.getArgument())){
                    Optional<RoomEntity> addedUserToRoom= roomRepository.findById(Long.valueOf(msg.getSubject()));
                    addedUserToRoom.ifPresent(room -> {
                        room.getUsers().add(userService.getUser(msg.getArgument()));
                        roomRepository.save(room);
                    });
                }
                break;
            case REMOVE_USER_FROM_ROOM:
                if(userService.existsUser(msg.getArgument())){
                    Optional<RoomEntity> removedUserToRoom = roomRepository.findById(Long.valueOf(msg.getSubject()));
                    removedUserToRoom.ifPresent(room -> {
                        var updatedUsers = room.getUsers().stream()
                                .filter(userEntity -> !Objects.equals(userEntity.getName(), msg.getArgument()))
                                .collect(Collectors.toSet());

                        room.setUsers(updatedUsers);
                        roomRepository.save(room);
                    });
                }
                break;
        }
    }

    private UserEntity createNewUser(UserInEvent msg) {
        return new UserEntity(msg.getSubject(), false, new HashSet<>());
    }
}
