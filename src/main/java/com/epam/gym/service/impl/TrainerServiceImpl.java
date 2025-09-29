package com.epam.gym.service.impl;

import com.epam.gym.dto.CreateTrainerDto;
import com.epam.gym.dto.UpdateTrainerDto;
import com.epam.gym.exception.NotFoundException;
import com.epam.gym.mapper.TrainerMapper;
import com.epam.gym.model.*;
import com.epam.gym.repository.TraineeRepository;
import com.epam.gym.repository.TrainerRepository;
import com.epam.gym.repository.TrainingTypeRepository;
import com.epam.gym.repository.UserRepository;
import com.epam.gym.service.TrainerService;
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
public class TrainerServiceImpl implements TrainerService {
    private TrainerRepository trainerRepository;
    private TrainerMapper trainerMapper;

    private TraineeRepository traineeRepository;
    private TrainingTypeRepository trainingTypeRepository;
    private UserRepository userRepository;

    @Override
    public List<Trainer> findAllTrainers() {
        return trainerRepository.findAll();
    }

    @Override
    public List<Trainer> findAllFreeTrainersByTraineeUsername(String username) {
        return trainerRepository.findFreeTrainersByTraineeUsername(username);
    }

    @Override
    public List<Trainer> findAllTrainers(CriteriaQuery<Trainer> criteria) {
        return trainerRepository.findAll(criteria);
    }

    @Override
    public Trainer findTrainerById(UUID id) {
        return trainerRepository.findByIdOrThrow(id);
    }

    @Override
    public Trainer findTrainerByUsername(String username) {
        return trainerRepository
                .findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainer with username " + username + " not found"));
    }

    @Override
    @Transactional
    public Trainer createTrainer(CreateTrainerDto trainerDto) {
        UUID userId = trainerDto.getUserId();
        User user = userRepository.findByIdOrThrow(userId);

        Trainer trainer = trainerMapper.toEntity(trainerDto);
        trainer.setUser(user);

        if (trainerDto.getSpecializationId() != null) {
            UUID trainingTypeId = trainerDto.getSpecializationId();
            TrainingType trainingType = trainingTypeRepository.findByIdOrThrow(trainingTypeId);

            trainer.setSpecialization(trainingType);
        }

        Trainer saved = trainerRepository.save(trainer.getId(), trainer);
        log.info("Trainer with username {} and id {} was created", user.getUsername(), trainer.getId());

        return saved;
    }

    private List<Trainee> getTrainees(List<UUID> traineeIds) {
        return traineeIds
                .stream()
                .map(id -> traineeRepository.findByIdOrThrow(id))
                .toList();
    }

    @Override
    @Transactional
    public Trainer updateTrainer(UUID id, UpdateTrainerDto trainerDto) {
        Trainer trainer = trainerRepository.findByIdOrThrow(id);

        trainerMapper.updateEntityFromDto(trainerDto, trainer);

        UUID userId = trainerDto.getUserId();
        User user = userRepository.findByIdOrThrow(userId);

        trainer.setUser(user);

        if (trainerDto.getTraineeIds() != null) {
            trainer.setTrainees(getTrainees(trainerDto.getTraineeIds()));
        }

        if (trainerDto.getSpecializationId() != null) {
            UUID trainingTypeId = trainerDto.getSpecializationId();
            TrainingType trainingType = trainingTypeRepository.findByIdOrThrow(trainingTypeId);

            trainer.setSpecialization(trainingType);
        }

        trainerRepository.save(id, trainer);
        log.debug("Trainer with username {} and id {} updated his data", user.getUsername(), trainer.getId());

        return trainer;
    }

    @Override
    @Transactional
    public Trainer deleteTrainer(UUID id) {
        Trainer trainer = trainerRepository.findByIdOrThrow(id);

        trainerRepository.delete(id);
        return trainer;
    }
}
