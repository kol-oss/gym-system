package com.epam.gym.controller;

import com.epam.gym.dto.CreateTraineeDto;
import com.epam.gym.dto.UpdateTraineeDto;
import com.epam.gym.model.Trainee;
import com.epam.gym.service.TraineeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class TraineeController {
    private AuthController authController;

    private TraineeService traineeService;

    public Trainee getByUsername(String username) {
        return traineeService.findTraineeByUsername(username);
    }

    public Trainee post(CreateTraineeDto trainerDto) {
        return traineeService.createTrainee(trainerDto);
    }

    public Trainee put(UUID id, UpdateTraineeDto trainerDto, String token) {
        authController.checkToken(token);

        return traineeService.updateTrainee(id, trainerDto);
    }
}
