package com.epam.gym.service.impl;

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
import com.epam.gym.service.TrainingService;
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
public class TrainingServiceImpl implements TrainingService {
    private TrainingRepository trainingRepository;
    private TrainingMapper trainingMapper;

    private TraineeRepository traineeRepository;
    private TrainerRepository trainerRepository;
    private TrainingTypeRepository trainingTypeRepository;

    @Override
    public List<Training> findAllTrainings() {
        return trainingRepository.findAll();
    }

    @Override
    public List<Training> findAllTrainings(CriteriaQuery<Training> criteria) {
        return trainingRepository.findAll(criteria);
    }

    @Override
    public Training findTrainingById(UUID id) {
        return trainingRepository.findByIdOrThrow(id);
    }

    @Override
    @Transactional
    public Training createTraining(CreateTrainingDto trainingDto) {
        if (trainingDto.getDuration() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }

        if (trainingDto.getDate() == null) {
            throw new IllegalArgumentException("Training date cannot be null");
        }

        Trainer trainer = trainerRepository.findByIdOrThrow(trainingDto.getTrainerId());
        Trainee trainee = traineeRepository.findByIdOrThrow(trainingDto.getTraineeId());
        TrainingType trainingType = trainingTypeRepository.findByIdOrThrow(trainingDto.getTrainingTypeId());

        Training training = trainingMapper.toEntity(trainingDto);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setType(trainingType);

        trainingRepository.save(training.getId(), training);
        log.info("Training between trainer {} and trainee {} on {} was created", trainer.getId(), trainee.getId(), training.getDate());

        return training;
    }

    @Override
    @Transactional
    public Training updateTraining(UUID id, UpdateTrainingDto trainingDto) {
        Training training = trainingRepository.findByIdOrThrow(id);

        if (trainingDto.getDuration() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }

        Trainer trainer = trainerRepository.findByIdOrThrow(trainingDto.getTrainerId());
        Trainee trainee = traineeRepository.findByIdOrThrow(trainingDto.getTraineeId());
        TrainingType trainingType = trainingTypeRepository.findByIdOrThrow(trainingDto.getTrainingTypeId());

        trainingMapper.updateEntityFromDto(trainingDto, training);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setType(trainingType);

        trainingRepository.save(training.getId(), training);
        log.info("Training between trainer {} and trainee {} was updated", trainer.getId(), trainee.getId());

        return training;
    }

    @Override
    @Transactional
    public Training deleteTraining(UUID id) {
        Training training = trainingRepository.findByIdOrThrow(id);

        trainingRepository.delete(id);
        return training;
    }
}
