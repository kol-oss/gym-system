package com.epam.gym.repository.impl;

import com.epam.gym.model.Trainer;
import com.epam.gym.repository.TrainerRepository;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TrainerRepositoryImpl extends CrudRepositoryImpl<Trainer, UUID> implements TrainerRepository {
    public TrainerRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Trainer.class);
    }

    @Override
    public List<Trainer> findFreeTrainersByTraineeUsername(String username) {
        Session session = getTransactionalSession();

        String rawQuery = """
                    SELECT tr
                    FROM Trainer tr
                    WHERE tr.id NOT IN (
                        SELECT trng.trainer.id
                        FROM Training trng
                        WHERE trng.trainee.user.username = :username
                    )
                """;

        TypedQuery<Trainer> query = session.createQuery(rawQuery, Trainer.class);
        query.setParameter("username", username);

        return query.getResultList();
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                        "from Trainer t where t.user.username = :username",
                        Trainer.class
                )
                .setParameter("username", username)
                .uniqueResultOptional();
    }
}
