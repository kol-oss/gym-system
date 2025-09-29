package com.epam.gym.controller;

import com.epam.gym.dto.CreateTrainingDto;
import com.epam.gym.model.Training;
import com.epam.gym.service.TrainingService;
import jakarta.persistence.criteria.CriteriaQuery;
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
class TrainingControllerTest {
    @Mock
    private AuthController authController;

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TrainingController trainingController;

    @Test
    void givenFiltersAndToken_whenGetAll_thenReturnTrainings() {
        // Arrange
        @SuppressWarnings("unchecked")
        CriteriaQuery<Training> filters = (CriteriaQuery<Training>) org.mockito.Mockito.mock(CriteriaQuery.class);
        String token = "validToken";
        List<Training> expected = List.of(new Training(), new Training());

        when(trainingService.findAllTrainings(filters)).thenReturn(expected);

        // Act
        List<Training> result = trainingController.getAll(filters, token);

        // Assert
        assertEquals(expected, result);
        verify(authController).checkToken(token);
        verify(trainingService).findAllTrainings(filters);
    }

    @Test
    void givenCreateTrainingDtoAndToken_whenPost_thenReturnCreatedTraining() {
        // Arrange
        CreateTrainingDto createDto = new CreateTrainingDto();
        String token = "validToken";
        Training training = new Training();

        when(trainingService.createTraining(createDto)).thenReturn(training);

        // Act
        Training result = trainingController.post(createDto, token);

        // Assert
        assertEquals(training, result);
        verify(authController).checkToken(token);
        verify(trainingService).createTraining(createDto);
    }
}