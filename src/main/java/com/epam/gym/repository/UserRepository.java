package com.epam.gym.repository;

import com.epam.gym.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    long countByUsernameLike(String username);
}
