package es.unizar.tmdad.service.impl;

import es.unizar.tmdad.controller.exception.UserNotFoundException;
import es.unizar.tmdad.repository.UserRepository;
import es.unizar.tmdad.repository.entity.UserEntity;
import es.unizar.tmdad.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean existsUser(String userName) {
        return userRepository.existsById(userName);
    }

    @Override
    public UserEntity getUser(String argument) {
        return userRepository.findById(argument).orElseThrow();
    }

    @Override
    public boolean isSuperUser(String email) throws UserNotFoundException {
        var user = userRepository.findById(email).orElseThrow(() -> new UserNotFoundException(email));
        return user.getSuperuser();
    }
}
