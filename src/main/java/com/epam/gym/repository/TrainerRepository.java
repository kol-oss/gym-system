package com.epam.gym.repository;

import com.epam.gym.model.Trainer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainerRepository extends CrudRepository<Trainer, UUID> {
    List<Trainer> findFreeTrainersByTraineeUsername(String username);

    Optional<Trainer> findByUsername(String username);
}
