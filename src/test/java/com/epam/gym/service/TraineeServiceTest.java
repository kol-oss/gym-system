package com.epam.gym.service;

import com.epam.gym.dto.CreateTraineeDto;
import com.epam.gym.dto.UpdateTraineeDto;
import com.epam.gym.exception.NotFoundException;
import com.epam.gym.mapper.TraineeMapper;
import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.User;
import com.epam.gym.repository.TraineeRepository;
import com.epam.gym.repository.TrainerRepository;
import com.epam.gym.repository.UserRepository;
import com.epam.gym.service.impl.TraineeServiceImpl;
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
public class TraineeServiceTest {
    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    public void givenTrainees_whenFindAll_thenReturnList() {
        // Arrange
        List<Trainee> trainees = List.of(new Trainee(), new Trainee());
        when(traineeRepository.findAll()).thenReturn(trainees);

        // Act
        List<Trainee> result = traineeService.findAllTrainees();

        // Assert
        assertEquals(trainees, result);
        verify(traineeRepository).findAll();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenCriteria_whenFindAll_thenReturnList() {
        // Arrange
        CriteriaQuery<Trainee> criteria = mock(CriteriaQuery.class);

        List<Trainee> trainees = List.of(new Trainee());
        when(traineeRepository.findAll(criteria)).thenReturn(trainees);

        // Act
        List<Trainee> result = traineeService.findAllTrainees(criteria);

        // Assert
        assertEquals(trainees, result);
        verify(traineeRepository).findAll(criteria);
    }

    @Test
    public void givenTraineeId_whenFindById_thenReturnTrainee() {
        // Arrange
        final UUID id = UUID.randomUUID();

        Trainee trainee = new Trainee();
        trainee.setId(id);

        when(traineeRepository.findByIdOrThrow(id)).thenReturn(trainee);

        // Act
        Trainee result = traineeService.findTraineeById(id);

        // Assert
        assertEquals(trainee, result);
        verify(traineeRepository).findByIdOrThrow(id);
    }

    @Test
    public void givenUsername_whenFindByUsername_thenReturnTrainee() {
        // Arrange
        final String username = "john.doe";

        User user = new User();
        user.setUsername(username);

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        // Act
        Trainee result = traineeService.findTraineeByUsername(username);

        // Assert
        assertEquals(trainee, result);
        verify(traineeRepository).findByUsername(username);
    }

    @Test
    public void givenNonExistingUsername_whenFindByUsername_thenThrowNotFound() {
        // Arrange
        final String username = "nonexistent";

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> traineeService.findTraineeByUsername(username));
        verify(traineeRepository).findByUsername(username);
    }

    @Test
    public void givenCreateTraineeDto_whenCreateTrainee_thenReturnTrainee() {
        // Arrange
        final UUID userId = UUID.randomUUID();
        final UUID traineeId = UUID.randomUUID();

        CreateTraineeDto createDto = new CreateTraineeDto();
        createDto.setUserId(userId);

        User user = new User();
        user.setId(userId);
        user.setUsername("john.doe");

        Trainee trainee = new Trainee();
        trainee.setId(traineeId);

        when(userRepository.findByIdOrThrow(userId)).thenReturn(user);
        when(traineeMapper.toEntity(createDto)).thenReturn(trainee);
        when(traineeRepository.save(traineeId, trainee)).thenReturn(trainee);

        // Act
        Trainee result = traineeService.createTrainee(createDto);

        // Assert
        assertEquals(trainee, result);
        assertEquals(user, result.getUser());

        verify(traineeRepository).save(trainee.getId(), trainee);
    }

    @Test
    public void givenUpdateTrainee_whenTraineeExists_thenUpdateAndReturn() {
        // Arrange
        final UUID traineeId = UUID.randomUUID();
        final UUID userId = UUID.randomUUID();
        final UUID trainerId = UUID.randomUUID();

        List<UUID> trainerIds = List.of(trainerId);

        UpdateTraineeDto updateDto = new UpdateTraineeDto();
        updateDto.setUserId(userId);
        updateDto.setTrainerIds(trainerIds);

        Trainee trainee = new Trainee();
        trainee.setId(traineeId);

        User user = new User();
        user.setId(userId);

        Trainer trainer = new Trainer();
        trainer.setId(trainerId);

        when(traineeRepository.findByIdOrThrow(traineeId)).thenReturn(trainee);
        when(userRepository.findByIdOrThrow(userId)).thenReturn(user);
        when(trainerRepository.findByIdOrThrow(trainerId)).thenReturn(trainer);

        // Act
        Trainee result = traineeService.updateTrainee(traineeId, updateDto);

        // Assert
        assertEquals(user, result.getUser());
        assertEquals(trainerIds.size(), result.getTrainers().size());
        assertEquals(trainer, result.getTrainers().getFirst());

        verify(traineeMapper).updateEntityFromDto(updateDto, trainee);
        verify(traineeRepository).save(traineeId, trainee);
    }

    @Test
    public void givenUpdateTrainee_whenTraineeNotFound_thenThrowNotFound() {
        // Arrange
        final UUID traineeId = UUID.randomUUID();

        UpdateTraineeDto updateDto = new UpdateTraineeDto();
        when(traineeRepository.findByIdOrThrow(traineeId)).thenThrow(new NotFoundException("Not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> traineeService.updateTrainee(traineeId, updateDto));
    }

    @Test
    public void givenDeleteTrainee_whenTraineeExists_thenReturnTrainee() {
        // Arrange
        final UUID traineeId = UUID.randomUUID();

        Trainee trainee = new Trainee();
        trainee.setId(traineeId);

        when(traineeRepository.findByIdOrThrow(traineeId)).thenReturn(trainee);

        // Act
        Trainee result = traineeService.deleteTrainee(traineeId);

        // Assert
        assertEquals(trainee, result);
        verify(traineeRepository).delete(traineeId);
    }

    @Test
    public void givenDeleteTrainee_whenTraineeNotFound_thenThrowNotFound() {
        // Arrange
        final UUID traineeId = UUID.randomUUID();

        when(traineeRepository.findByIdOrThrow(traineeId)).thenThrow(new NotFoundException("Not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> traineeService.deleteTrainee(traineeId));
    }
}
