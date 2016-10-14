package ru.askor.blagosfera.data.redis.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.askor.blagosfera.data.redis.entities.UserSessionEntity;

import java.util.List;

/**
 * Created by vtarasenko on 28.04.2016.
 */
public interface UserSessionRepository extends CrudRepository<UserSessionEntity,String> {
    List<UserSessionEntity> findByUsername(String username);
}
