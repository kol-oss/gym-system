package com.epam.gym.repository.impl;

import com.epam.gym.model.TrainingType;
import com.epam.gym.repository.TrainingTypeRepository;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class TrainingTypeRepositoryImpl extends CrudRepositoryImpl<TrainingType, UUID> implements TrainingTypeRepository {
    public TrainingTypeRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, TrainingType.class);
    }

    @Override
    public TrainingType save(UUID key, TrainingType value) {
        throw new UnsupportedOperationException("Training types can not be updated from Application");
    }

    @Override
    public void delete(UUID key) {
        throw new UnsupportedOperationException("Training types can not be updated from Application");
    }
}
