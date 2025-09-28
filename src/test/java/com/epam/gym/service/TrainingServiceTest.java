package com.epam.gym.service;

import com.epam.gym.dto.CreateTrainingDto;
import com.epam.gym.dto.UpdateTrainingDto;
import com.epam.gym.mapper.TrainingMapper;
import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;
import com.epam.gym.model.TrainingType;
import com.epam.gym.repository.TraineeRepository;
import com.epam.gym.repository.TrainerRepository;
import com.epam.gym.repository.TrainingRepository;
import com.epam.gym.repository.TrainingTypeRepository;
import com.epam.gym.service.impl.TrainingServiceImpl;
import jakarta.persistence.criteria.CriteriaQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceTest {
    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainingMapper trainingMapper;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    public void givenTrainings_whenFindAll_thenReturnList() {
        // Arrange
        List<Training> trainings = List.of(new Training(), new Training());
        when(trainingRepository.findAll()).thenReturn(trainings);

        // Act
        List<Training> result = trainingService.findAllTrainings();

        // Assert
        assertEquals(trainings, result);
        verify(trainingRepository).findAll();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenCriteria_whenFindAll_thenReturnList() {
        // Arrange
        CriteriaQuery<Training> criteria = mock(CriteriaQuery.class);
        List<Training> trainings = List.of(new Training());

        when(trainingRepository.findAll(criteria)).thenReturn(trainings);

        // Act
        List<Training> result = trainingService.findAllTrainings(criteria);

        // Assert
        assertEquals(trainings, result);
        verify(trainingRepository).findAll(criteria);
    }

    @Test
    public void givenTrainingId_whenFindById_thenReturnTraining() {
        // Arrange
        final UUID id = UUID.randomUUID();

        Training training = new Training();
        training.setId(id);

        when(trainingRepository.findByIdOrThrow(id)).thenReturn(training);

        // Act
        Training result = trainingService.findTrainingById(id);

        // Assert
        assertEquals(training, result);
        verify(trainingRepository).findByIdOrThrow(id);
    }

    @Test
    public void givenValidDto_whenCreateTraining_thenReturnTraining() {
        // Arrange
        final UUID trainerId = UUID.randomUUID();
        final UUID traineeId = UUID.randomUUID();
        final UUID typeId = UUID.randomUUID();

        CreateTrainingDto createDto = new CreateTrainingDto();
        createDto.setTrainerId(trainerId);
        createDto.setTraineeId(traineeId);
        createDto.setTrainingTypeId(typeId);
        createDto.setDuration(60);
        createDto.setDate(LocalDate.now());

        Trainer trainer = new Trainer();
        trainer.setId(trainerId);

        Trainee trainee = new Trainee();
        trainee.setId(traineeId);

        TrainingType type = new TrainingType();
        type.setId(typeId);

        Training training = new Training();
        training.setId(UUID.randomUUID());

        when(trainerRepository.findByIdOrThrow(trainerId)).thenReturn(trainer);
        when(traineeRepository.findByIdOrThrow(traineeId)).thenReturn(trainee);
        when(trainingTypeRepository.findByIdOrThrow(typeId)).thenReturn(type);
        when(trainingMapper.toEntity(createDto)).thenReturn(training);

        // Act
        Training result = trainingService.createTraining(createDto);

        // Assert
        assertEquals(training, result);
        assertEquals(trainer, result.getTrainer());
        assertEquals(trainee, result.getTrainee());
        assertEquals(type, result.getType());

        verify(trainingRepository).save(training.getId(), training);
    }

    @Test
    public void givenInvalidDuration_whenCreateTraining_thenThrowException() {
        // Arrange
        final CreateTrainingDto dto = new CreateTrainingDto();
        dto.setDuration(0);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(dto));
        verifyNoInteractions(trainerRepository, traineeRepository, trainingTypeRepository);
    }

    @Test
    public void givenValidDto_whenUpdateTraining_thenReturnUpdatedTraining() {
        // Arrange
        final UUID id = UUID.randomUUID();
        final UUID trainerId = UUID.randomUUID();
        final UUID traineeId = UUID.randomUUID();
        final UUID typeId = UUID.randomUUID();

        UpdateTrainingDto updateDto = new UpdateTrainingDto();
        updateDto.setTrainerId(trainerId);
        updateDto.setTraineeId(traineeId);
        updateDto.setTrainingTypeId(typeId);
        updateDto.setDuration(90);

        Training training = new Training();
        training.setId(id);

        Trainer trainer = new Trainer();
        trainer.setId(trainerId);

        Trainee trainee = new Trainee();
        trainee.setId(traineeId);

        TrainingType type = new TrainingType();
        type.setId(typeId);

        when(trainingRepository.findByIdOrThrow(id)).thenReturn(training);
        when(trainerRepository.findByIdOrThrow(trainerId)).thenReturn(trainer);
        when(traineeRepository.findByIdOrThrow(traineeId)).thenReturn(trainee);
        when(trainingTypeRepository.findByIdOrThrow(typeId)).thenReturn(type);

        // Act
        Training result = trainingService.updateTraining(id, updateDto);

        // Assert
        assertEquals(training, result);
        assertEquals(trainer, result.getTrainer());
        assertEquals(trainee, result.getTrainee());
        assertEquals(type, result.getType());

        verify(trainingMapper).updateEntityFromDto(updateDto, training);
        verify(trainingRepository).save(id, training);
    }

    @Test
    public void givenInvalidDuration_whenUpdateTraining_thenThrowException() {
        // Arrange
        final UUID id = UUID.randomUUID();

        UpdateTrainingDto updateDto = new UpdateTrainingDto();
        updateDto.setDuration(0);

        Training training = new Training();
        when(trainingRepository.findByIdOrThrow(id)).thenReturn(training);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainingService.updateTraining(id, updateDto));

        verify(trainingRepository).findByIdOrThrow(id);
        verifyNoMoreInteractions(trainingRepository);
    }

    @Test
    public void givenId_whenDeleteTraining_thenReturnDeletedTraining() {
        // Arrange
        final UUID id = UUID.randomUUID();

        Training training = new Training();
        training.setId(id);

        when(trainingRepository.findByIdOrThrow(id)).thenReturn(training);

        // Act
        Training result = trainingService.deleteTraining(id);

        // Assert
        assertEquals(training, result);
        verify(trainingRepository).delete(id);
    }
}
