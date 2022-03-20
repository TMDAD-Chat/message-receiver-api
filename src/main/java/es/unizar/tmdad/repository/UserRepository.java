package es.unizar.tmdad.repository;

import es.unizar.tmdad.repository.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, String> {
}
