package es.unizar.tmdad.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.tmdad.adt.user.UserInEvent;
import es.unizar.tmdad.repository.*;
import es.unizar.tmdad.repository.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageListener {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    @Value("${chat.exchanges.output:message-pcs}")
    private String topicExchangeName;

    public MessageListener(ObjectMapper objectMapper, UserRepository userRepository) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
    }

    private void logs(UserInEvent in){
        log.info("Processing user event {}.", in);
    }

    public void apply(String input) throws JsonProcessingException {
        UserInEvent msg = objectMapper.readValue(input, UserInEvent.class);
        this.logs(msg);

        //TODO SAVE TO DB
        switch (msg.getEvent()){

            case ADD_USER:
                UserEntity addedUser = UserEntity.builder().name(msg.getSubject()).build();
                userRepository.save(addedUser);
                break;
            case DELETE_USER:
                userRepository.deleteById(msg.getSubject());
                break;
            case ADD_ROOM:
                break;
            case DELETE_ROOM:
                break;
            case ADD_USER_TO_ROOM:
                break;
            case REMOVE_USER_FROM_ROOM:
                break;
        }
    }
}
