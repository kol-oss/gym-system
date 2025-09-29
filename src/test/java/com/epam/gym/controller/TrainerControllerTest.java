package com.epam.gym.controller;

import com.epam.gym.dto.CreateTrainerDto;
import com.epam.gym.model.Trainer;
import com.epam.gym.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    @Mock
    private AuthController authController;

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainerController trainerController;

    @Test
    void givenTraineeUsernameAndToken_whenGetFreeTrainers_thenReturnTrainerList() {
        // Arrange
        final String traineeUsername = "john.doe";
        final String token = "validToken";
        List<Trainer> expected = List.of(new Trainer(), new Trainer());

        when(trainerService.findAllFreeTrainersByTraineeUsername(traineeUsername)).thenReturn(expected);

        // Act
        List<Trainer> result = trainerController.getFreeTrainersByTraineeUsername(traineeUsername, token);

        // Assert
        assertEquals(expected, result);
        verify(authController).checkToken(token);
        verify(trainerService).findAllFreeTrainersByTraineeUsername(traineeUsername);
    }

    @Test
    void givenUsername_whenGetByUsername_thenReturnTrainer() {
        // Arrange
        final String username = "trainer.one";
        Trainer trainer = new Trainer();

        when(trainerService.findTrainerByUsername(username)).thenReturn(trainer);

        // Act
        Trainer result = trainerController.getByUsername(username);

        // Assert
        assertEquals(trainer, result);
        verify(trainerService).findTrainerByUsername(username);
    }

    @Test
    void givenCreateTrainerDto_whenPost_thenReturnTrainer() {
        // Arrange
        CreateTrainerDto createDto = new CreateTrainerDto();
        Trainer trainer = new Trainer();

        when(trainerService.createTrainer(createDto)).thenReturn(trainer);

        // Act
        Trainer result = trainerController.post(createDto);

        // Assert
        assertEquals(trainer, result);
        verify(trainerService).createTrainer(createDto);
    }
}