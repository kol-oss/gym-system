package com.epam.gym;

import com.epam.gym.config.AppConfig;
import com.epam.gym.controller.*;
import com.epam.gym.dto.*;
import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;
import com.epam.gym.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            EntityManagerFactory entityManagerFactory = context.getBean(EntityManagerFactory.class);
            EntityManager entityManager = entityManagerFactory.createEntityManager();

            AuthController authController = context.getBean(AuthController.class);
            UserController userController = context.getBean(UserController.class);

            TrainerController trainerController = context.getBean(TrainerController.class);
            TraineeController traineeController = context.getBean(TraineeController.class);
            TrainingController trainingController = context.getBean(TrainingController.class);

            // 0. User creation
            CreateUserDto userDto = new CreateUserDto();
            userDto.setFirstName("John");
            userDto.setLastName("Doe");

            CreateUserResponse response = userController.post(userDto);
            String username = response.getUsername();
            String password = response.getPassword();

            // 0. User retrieval
            // String username = "John.Doe";
            // String password = "h0G&?8!eOr";

            User user = userController.getByUsername(username);

            // 1. Trainer creation
            UUID trainingTypeId = UUID.fromString("a1b3c5d8-214a-4894-8000-639d0178d20d");

            CreateTrainerDto trainerDto = new CreateTrainerDto();
            trainerDto.setUserId(user.getId());
            trainerDto.setSpecializationId(trainingTypeId);

            trainerController.post(trainerDto);

            // 2. Trainee creation
            CreateTraineeDto traineeDto = new CreateTraineeDto();
            traineeDto.setUserId(user.getId());
            traineeDto.setAddress("Kyiv, st. 5");
            traineeDto.setBirthDate(LocalDate.now());

            traineeController.post(traineeDto);

            // 3 & 4. User login
            String token = authController.login(username, password);

            // 5. Select Trainer profile by username.
            Trainer trainer = trainerController.getByUsername(username);

            // 6. Select Trainee profile by username.
            Trainee trainee = traineeController.getByUsername(username);

            // 7 & 8. Password change.
            password = "12345";
            userController.patchPassword(user.getId(), password, token);

            // 9 & 10. Update trainer/trainee profile.
            UpdateTraineeDto updateTraineeDto = new UpdateTraineeDto();
            updateTraineeDto.setAddress("Lviv, st. 10");

            traineeController.put(trainee.getId(), updateTraineeDto, token);

            // 11 & 12. Activate/De-activate trainer/trainee.
            userController.patchStatus(user.getId(), false, token);

            // 13. Delete trainee profile by username.
            userController.deleteByUsername(username, token);

            // 14 & 15. Get Trainings List by filters.
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Training> filters = cb.createQuery(Training.class);
            Root<Training> root = filters.from(Training.class);

            List<Predicate> predicates = List.of(
                    cb.equal(root.get("trainee").get("username"), username),
                    cb.greaterThanOrEqualTo(root.get("date"), LocalDate.of(2025, 1, 1)),
                    cb.lessThanOrEqualTo(root.get("date"), LocalDate.of(2025, 12, 11)),
                    cb.equal(root.get("trainer").get("name"), username)
            );

            filters.select(root)
                    .where(cb.and(predicates.toArray(Predicate[]::new)))
                    .orderBy(cb.asc(root.get("date")));

            trainingController.getAll(filters, token);

            // 16. Add training.
            CreateTrainingDto trainingDto = new CreateTrainingDto();
            trainingDto.setName("Regular training");
            trainingDto.setDuration(5);
            trainingDto.setTrainingTypeId(trainingTypeId);
            trainingDto.setTrainerId(trainer.getId());
            trainingDto.setTraineeId(trainee.getId());

            trainingController.post(trainingDto, token);

            // 17. Get trainers list that not assigned on trainee by trainee's username.
            List<Trainer> freeTrainers = trainerController.getFreeTrainersByTraineeUsername(username, token);

            // 18. Update Tranee's trainers list
            List<UUID> trainersForTrainee = List.of(trainer.getId());

            UpdateTraineeDto updateTrainersDto = new UpdateTraineeDto();
            updateTrainersDto.setTrainerIds(trainersForTrainee);

            traineeController.put(trainee.getId(), updateTraineeDto, token);
        }
    }
}
