package com.epam.gym.service.impl;

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
import com.epam.gym.service.TraineeService;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class TraineeServiceImpl implements TraineeService {
    private TraineeRepository traineeRepository;
    private TraineeMapper traineeMapper;

    private TrainerRepository trainerRepository;
    private UserRepository userRepository;

    @Override
    public List<Trainee> findAllTrainees() {
        return traineeRepository.findAll();
    }

    @Override
    public List<Trainee> findAllTrainees(CriteriaQuery<Trainee> criteria) {
        return traineeRepository.findAll(criteria);
    }

    @Override
    public Trainee findTraineeById(UUID id) {
        return traineeRepository.findByIdOrThrow(id);
    }

    @Override
    public Trainee findTraineeByUsername(String username) {
        return traineeRepository
                .findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee with username " + username + " not found"));
    }

    @Override
    @Transactional
    public Trainee createTrainee(CreateTraineeDto traineeDto) {
        UUID userId = traineeDto.getUserId();
        User user = userRepository.findByIdOrThrow(userId);

        Trainee trainee = traineeMapper.toEntity(traineeDto);
        trainee.setUser(user);

        traineeRepository.save(trainee.getId(), trainee);
        log.info("Trainee with username {} and id {} was created", user.getUsername(), trainee.getId());

        return trainee;
    }

    private List<Trainer> getTrainers(List<UUID> trainerIds) {
        return trainerIds
                .stream()
                .map(id -> trainerRepository.findByIdOrThrow(id))
                .toList();
    }

    @Override
    @Transactional
    public Trainee updateTrainee(UUID id, UpdateTraineeDto traineeDto) {
        Trainee trainee = traineeRepository.findByIdOrThrow(id);
        traineeMapper.updateEntityFromDto(traineeDto, trainee);

        UUID userId = traineeDto.getUserId();
        User user = userRepository.findByIdOrThrow(userId);
        trainee.setUser(user);

        if (traineeDto.getTrainerIds() != null) {
            trainee.setTrainers(getTrainers(traineeDto.getTrainerIds()));
        }

        traineeRepository.save(id, trainee);
        log.debug("Trainee with username {} and id {} updated his data", user.getUsername(), trainee.getId());

        return trainee;
    }

    @Override
    @Transactional
    public Trainee deleteTrainee(UUID id) {
        Trainee trainee = traineeRepository.findByIdOrThrow(id);

        traineeRepository.delete(id);
        return trainee;
    }
}