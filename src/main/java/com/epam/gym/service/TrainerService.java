package com.epam.gym.service;

import com.epam.gym.dto.CreateTrainerDto;
import com.epam.gym.dto.UpdateTrainerDto;
import com.epam.gym.model.Trainer;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.List;
import java.util.UUID;

public interface TrainerService {
    List<Trainer> findAllTrainers();

    List<Trainer> findAllFreeTrainersByTraineeUsername(String username);

    List<Trainer> findAllTrainers(CriteriaQuery<Trainer> criteria);

    Trainer findTrainerById(UUID id);

    Trainer findTrainerByUsername(String username);

    Trainer createTrainer(CreateTrainerDto trainerDto);

    Trainer updateTrainer(UUID id, UpdateTrainerDto trainerDto);

    Trainer deleteTrainer(UUID id);
}
