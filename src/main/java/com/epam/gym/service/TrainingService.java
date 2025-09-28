package com.epam.gym.service;

import com.epam.gym.dto.CreateTrainingDto;
import com.epam.gym.dto.UpdateTrainingDto;
import com.epam.gym.model.Training;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.List;
import java.util.UUID;

public interface TrainingService {
    List<Training> findAllTrainings();

    List<Training> findAllTrainings(CriteriaQuery<Training> criteria);

    Training findTrainingById(UUID id);

    Training createTraining(CreateTrainingDto trainingDto);

    Training updateTraining(UUID id, UpdateTrainingDto trainingDto);

    Training deleteTraining(UUID id);
}
