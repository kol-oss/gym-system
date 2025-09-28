package com.epam.gym.repository.impl;

import com.epam.gym.model.Trainee;
import com.epam.gym.repository.TraineeRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class TraineeRepositoryImpl extends CrudRepositoryImpl<Trainee, UUID> implements TraineeRepository {
    public TraineeRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Trainee.class);
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                        "from Trainee t where t.user.username = :username",
                        Trainee.class
                )
                .setParameter("username", username)
                .uniqueResultOptional();
    }
}
