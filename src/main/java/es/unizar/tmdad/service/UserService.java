package es.unizar.tmdad.service;

import es.unizar.tmdad.repository.entity.UserEntity;

public interface UserService {

    boolean existsUser(String userId);

    UserEntity getUser(String argument);
}
