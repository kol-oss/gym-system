package com.epam.gym.repository.impl;

import com.epam.gym.model.Training;
import com.epam.gym.repository.TrainingRepository;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class TrainingRepositoryImpl extends CrudRepositoryImpl<Training, UUID> implements TrainingRepository {
    public TrainingRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Training.class);
    }
}
