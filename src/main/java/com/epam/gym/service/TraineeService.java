package com.epam.gym.service;

import com.epam.gym.dto.CreateTraineeDto;
import com.epam.gym.dto.UpdateTraineeDto;
import com.epam.gym.model.Trainee;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.List;
import java.util.UUID;

public interface TraineeService {
    List<Trainee> findAllTrainees();

    List<Trainee> findAllTrainees(CriteriaQuery<Trainee> criteria);

    Trainee findTraineeById(UUID id);

    Trainee findTraineeByUsername(String username);

    Trainee createTrainee(CreateTraineeDto traineeDto);

    Trainee updateTrainee(UUID id, UpdateTraineeDto traineeDto);

    Trainee deleteTrainee(UUID id);
}
