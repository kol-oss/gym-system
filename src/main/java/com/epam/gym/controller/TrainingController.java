package com.epam.gym.controller;

import com.epam.gym.dto.CreateTrainingDto;
import com.epam.gym.model.Training;
import com.epam.gym.service.TrainingService;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TrainingController {
    private AuthController authController;
    private TrainingService trainingService;

    public List<Training> getAll(CriteriaQuery<Training> filters, String token) {
        authController.checkToken(token);

        return trainingService.findAllTrainings(filters);
    }

    public Training post(CreateTrainingDto trainingDto, String token) {
        authController.checkToken(token);

        return trainingService.createTraining(trainingDto);
    }
}
