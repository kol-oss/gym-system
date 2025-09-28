package com.epam.gym.service;

import com.epam.gym.dto.CreateTrainerDto;
import com.epam.gym.dto.UpdateTrainerDto;
import com.epam.gym.exception.NotFoundException;
import com.epam.gym.mapper.TrainerMapper;
import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.TrainingType;
import com.epam.gym.model.User;
import com.epam.gym.repository.TraineeRepository;
import com.epam.gym.repository.TrainerRepository;
import com.epam.gym.repository.TrainingTypeRepository;
import com.epam.gym.repository.UserRepository;
import com.epam.gym.service.impl.TrainerServiceImpl;
import jakarta.persistence.criteria.CriteriaQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceTest {
    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    public void givenTrainers_whenFindAll_thenReturnList() {
        // Arrange
        List<Trainer> trainers = List.of(new Trainer(), new Trainer());
        when(trainerRepository.findAll()).thenReturn(trainers);

        // Act
        List<Trainer> result = trainerService.findAllTrainers();

        // Assert
        assertEquals(trainers, result);
        verify(trainerRepository).findAll();
    }

    @Test
    public void givenUsername_whenFindAllFreeTrainers_thenReturnList() {
        // Arrange
        final String username = "john.doe";

        List<Trainer> trainers = List.of(new Trainer());
        when(trainerRepository.findFreeTrainersByTraineeUsername(username)).thenReturn(trainers);

        // Act
        List<Trainer> result = trainerService.findAllFreeTrainersByTraineeUsername(username);

        // Assert
        assertEquals(trainers, result);
        verify(trainerRepository).findFreeTrainersByTraineeUsername(username);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenCriteria_whenFindAll_thenReturnList() {
        // Arrange
        CriteriaQuery<Trainer> criteria = mock(CriteriaQuery.class);
        List<Trainer> trainers = List.of(new Trainer());
        when(trainerRepository.findAll(criteria)).thenReturn(trainers);

        // Act
        List<Trainer> result = trainerService.findAllTrainers(criteria);

        // Assert
        assertEquals(trainers, result);
        verify(trainerRepository).findAll(criteria);
    }

    @Test
    public void givenTrainerId_whenFindById_thenReturnTrainer() {
        // Arrange
        final UUID id = UUID.randomUUID();

        Trainer trainer = new Trainer();
        trainer.setId(id);

        when(trainerRepository.findByIdOrThrow(id)).thenReturn(trainer);

        // Act
        Trainer result = trainerService.findTrainerById(id);

        // Assert
        assertEquals(trainer, result);
        verify(trainerRepository).findByIdOrThrow(id);
    }

    @Test
    public void givenUsername_whenFindByUsername_thenReturnTrainer() {
        // Arrange
        final String username = "john.doe";

        User user = new User();
        user.setUsername(username);

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));

        // Act
        Trainer result = trainerService.findTrainerByUsername(username);

        // Assert
        assertEquals(trainer, result);
        verify(trainerRepository).findByUsername(username);
    }

    @Test
    public void givenNonExistingUsername_whenFindByUsername_thenThrowNotFound() {
        // Arrange
        final String username = "nonexistent";

        when(trainerRepository.findByUsername(username)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> trainerService.findTrainerByUsername(username));
        verify(trainerRepository).findByUsername(username);
    }

    @Test
    public void givenCreateTrainerDto_whenCreateTrainer_thenReturnTrainer() {
        // Arrange
        final UUID userId = UUID.randomUUID();
        final UUID specializationId = UUID.randomUUID();

        CreateTrainerDto createDto = new CreateTrainerDto();
        createDto.setUserId(userId);
        createDto.setSpecializationId(specializationId);

        User user = new User();
        user.setId(userId);
        user.setUsername("john.doe");

        TrainingType trainingType = new TrainingType();
        trainingType.setId(specializationId);

        Trainer trainer = new Trainer();
        trainer.setId(UUID.randomUUID());

        when(userRepository.findByIdOrThrow(userId)).thenReturn(user);
        when(trainingTypeRepository.findByIdOrThrow(specializationId)).thenReturn(trainingType);
        when(trainerMapper.toEntity(createDto)).thenReturn(trainer);

        // Act
        Trainer result = trainerService.createTrainer(createDto);

        // Assert
        assertEquals(trainer, result);
        assertEquals(user, result.getUser());
        assertEquals(trainingType, result.getSpecialization());

        verify(trainerRepository).save(trainer.getId(), trainer);
    }

    @Test
    public void givenUpdateTrainer_whenTrainerExists_thenUpdateAndReturn() {
        // Arrange
        final UUID trainerId = UUID.randomUUID();
        final UUID userId = UUID.randomUUID();
        final UUID traineeId = UUID.randomUUID();
        final UUID specializationId = UUID.randomUUID();

        UpdateTrainerDto updateDto = new UpdateTrainerDto();
        updateDto.setUserId(userId);
        updateDto.setTraineeIds(List.of(traineeId));
        updateDto.setSpecializationId(specializationId);

        Trainer trainer = new Trainer();
        trainer.setId(trainerId);

        User user = new User();
        user.setId(userId);

        Trainee trainee = new Trainee();
        trainee.setId(traineeId);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(specializationId);

        when(trainerRepository.findByIdOrThrow(trainerId)).thenReturn(trainer);
        when(userRepository.findByIdOrThrow(userId)).thenReturn(user);
        when(traineeRepository.findByIdOrThrow(traineeId)).thenReturn(trainee);
        when(trainingTypeRepository.findByIdOrThrow(specializationId)).thenReturn(trainingType);

        // Act
        Trainer result = trainerService.updateTrainer(trainerId, updateDto);

        // Assert
        assertEquals(user, result.getUser());
        assertEquals(1, result.getTrainees().size());
        assertEquals(trainee, result.getTrainees().getFirst());
        assertEquals(trainingType, result.getSpecialization());

        verify(trainerMapper).updateEntityFromDto(updateDto, trainer);
        verify(trainerRepository).save(trainerId, trainer);
    }

    @Test
    public void givenUpdateTrainer_whenTrainerNotFound_thenThrowNotFound() {
        // Arrange
        final UUID trainerId = UUID.randomUUID();

        UpdateTrainerDto updateDto = new UpdateTrainerDto();
        when(trainerRepository.findByIdOrThrow(trainerId)).thenThrow(new NotFoundException("Not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> trainerService.updateTrainer(trainerId, updateDto));
    }

    @Test
    public void givenDeleteTrainer_whenTrainerExists_thenReturnTrainer() {
        // Arrange
        final UUID trainerId = UUID.randomUUID();

        Trainer trainer = new Trainer();
        trainer.setId(trainerId);

        when(trainerRepository.findByIdOrThrow(trainerId)).thenReturn(trainer);

        // Act
        Trainer result = trainerService.deleteTrainer(trainerId);

        // Assert
        assertEquals(trainer, result);
        verify(trainerRepository).delete(trainerId);
    }

    @Test
    public void givenDeleteTrainer_whenTrainerNotFound_thenThrowNotFound() {
        // Arrange
        final UUID trainerId = UUID.randomUUID();

        when(trainerRepository.findByIdOrThrow(trainerId)).thenThrow(new NotFoundException("Not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> trainerService.deleteTrainer(trainerId));
    }
}