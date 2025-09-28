package com.epam.gym.controller;

import com.epam.gym.dto.CreateTrainerDto;
import com.epam.gym.model.Trainer;
import com.epam.gym.service.TrainerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TrainerController {
    private AuthController authController;
    private TrainerService trainerService;

    public List<Trainer> getFreeTrainersByTraineeUsername(String traineeUsername, String token) {
        authController.checkToken(token);

        return trainerService.findAllFreeTrainersByTraineeUsername(traineeUsername);
    }

    public Trainer getByUsername(String username) {
        return trainerService.findTrainerByUsername(username);
    }

    public Trainer post(CreateTrainerDto trainerDto) {
        return trainerService.createTrainer(trainerDto);
    }
}
