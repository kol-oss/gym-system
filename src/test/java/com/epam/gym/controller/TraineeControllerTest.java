package com.epam.gym.controller;

import com.epam.gym.dto.CreateTraineeDto;
import com.epam.gym.dto.UpdateTraineeDto;
import com.epam.gym.model.Trainee;
import com.epam.gym.service.TraineeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TraineeControllerTest {
    @Mock
    private AuthController authController;

    @Mock
    private TraineeService traineeService;

    @InjectMocks
    private TraineeController traineeController;

    @Test
    public void givenUsername_whenGetByUsername_thenReturnTrainee() {
        // Arrange
        final String username = "john.doe";
        Trainee trainee = new Trainee();

        when(traineeService.findTraineeByUsername(username)).thenReturn(trainee);

        // Act
        Trainee result = traineeController.getByUsername(username);

        // Assert
        assertEquals(trainee, result);
        verify(traineeService).findTraineeByUsername(username);
    }

    @Test
    public void givenCreateTraineeDto_whenPost_thenReturnTrainee() {
        // Arrange
        CreateTraineeDto createDto = new CreateTraineeDto();
        Trainee trainee = new Trainee();

        when(traineeService.createTrainee(createDto)).thenReturn(trainee);

        // Act
        Trainee result = traineeController.post(createDto);

        // Assert
        assertEquals(trainee, result);
        verify(traineeService).createTrainee(createDto);
    }

    @Test
    public void givenIdUpdateDtoAndToken_whenPut_thenReturnUpdatedTrainee() {
        // Arrange
        final UUID id = UUID.randomUUID();
        final String token = "token";

        UpdateTraineeDto updateDto = new UpdateTraineeDto();
        Trainee trainee = new Trainee();

        when(traineeService.updateTrainee(id, updateDto)).thenReturn(trainee);

        // Act
        Trainee result = traineeController.put(id, updateDto, token);

        // Assert
        assertEquals(trainee, result);
        verify(authController).checkToken(token);
        verify(traineeService).updateTrainee(id, updateDto);
    }
}
