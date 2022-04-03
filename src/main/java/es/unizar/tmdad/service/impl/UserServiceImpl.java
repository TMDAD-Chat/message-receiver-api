package es.unizar.tmdad.service.impl;

import es.unizar.tmdad.repository.UserRepository;
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
        return userRepository.findById(userName).isPresent();
    }
}
