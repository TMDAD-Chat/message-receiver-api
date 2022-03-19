package es.unizar.tmdad.service.impl;

import es.unizar.tmdad.repository.*;
import es.unizar.tmdad.service.*;
import org.springframework.stereotype.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean existsUser(String userId) {
        return userRepository.findById(Long.valueOf(userId)).isPresent();
    }
}
