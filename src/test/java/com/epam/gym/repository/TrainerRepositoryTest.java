package com.epam.gym.repository;

import com.epam.gym.model.Trainer;
import com.epam.gym.repository.impl.TrainerRepositoryImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerRepositoryTest {
    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Query<Trainer> query;

    private TrainerRepositoryImpl repository;

    @BeforeEach
    public void setUp() {
        repository = new TrainerRepositoryImpl(sessionFactory);
    }

    @Test
    public void givenTrainers_whenFindFreeTrainersByTraineeUsername_thenReturnTrainers() {
        // Arrange
        final String username = "john_doe";
        final String parameter = "username";

        List<Trainer> trainers = List.of(new Trainer());

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        String rawQuery = """
                    SELECT tr
                    FROM Trainer tr
                    WHERE tr.id NOT IN (
                        SELECT trng.trainer.id
                        FROM Training trng
                        WHERE trng.trainee.user.username = :username
                    )
                """;
        when(session.createQuery(rawQuery, Trainer.class)).thenReturn(query);
        when(query.setParameter(parameter, username)).thenReturn(query);
        when(query.getResultList()).thenReturn(trainers);

        // Act
        List<Trainer> result = repository.findFreeTrainersByTraineeUsername(username);

        // Assert
        assertEquals(trainers, result);

        verify(session).createQuery(rawQuery, Trainer.class);
        verify(query).setParameter(parameter, username);
        verify(query).getResultList();
    }

    @Test
    public void givenNoTrainers_whenFindFreeTrainersByTraineeUsername_thenReturnEmptyList() {
        // Arrange
        final String username = "unknown_user";
        final String parameter = "username";

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        String rawQuery = """
                    SELECT tr
                    FROM Trainer tr
                    WHERE tr.id NOT IN (
                        SELECT trng.trainer.id
                        FROM Training trng
                        WHERE trng.trainee.user.username = :username
                    )
                """;
        when(session.createQuery(rawQuery, Trainer.class)).thenReturn(query);
        when(query.setParameter(parameter, username)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        // Act
        List<Trainer> result = repository.findFreeTrainersByTraineeUsername(username);

        // Assert
        assertTrue(result.isEmpty());

        verify(session).createQuery(rawQuery, Trainer.class);
        verify(query).setParameter(parameter, username);
        verify(query).getResultList();
    }

    @Test
    public void givenTrainer_whenFindByUsername_thenReturnTrainer() {
        // Arrange
        final String username = "john_doe";
        final String parameter = "username";

        Trainer trainer = new Trainer();
        trainer.setId(UUID.randomUUID());

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createQuery(any(), eq(Trainer.class))).thenReturn(query);
        when(query.setParameter(parameter, username)).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.of(trainer));

        // Act
        Optional<Trainer> result = repository.findByUsername(username);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(trainer, result.get());
        verify(session).createQuery("from Trainer t where t.user.username = :username", Trainer.class);
        verify(query).setParameter(parameter, username);
        verify(query).uniqueResultOptional();
    }

    @Test
    public void givenNoTrainer_whenFindByUsername_thenReturnEmpty() {
        // Arrange
        final String username = "unknown_user";
        final String parameter = "username";

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createQuery(any(), eq(Trainer.class))).thenReturn(query);
        when(query.setParameter(parameter, username)).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.empty());

        // Act
        Optional<Trainer> result = repository.findByUsername(username);

        // Assert
        assertFalse(result.isPresent());
        verify(session).createQuery("from Trainer t where t.user.username = :username", Trainer.class);
        verify(query).setParameter(parameter, username);
        verify(query).uniqueResultOptional();
    }
}