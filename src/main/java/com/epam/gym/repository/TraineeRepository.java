package com.epam.gym.repository;

import com.epam.gym.model.Trainee;

import java.util.Optional;
import java.util.UUID;

public interface TraineeRepository extends CrudRepository<Trainee, UUID> {
    Optional<Trainee> findByUsername(String username);
}
