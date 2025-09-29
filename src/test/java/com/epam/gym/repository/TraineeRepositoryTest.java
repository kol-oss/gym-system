package com.epam.gym.repository;

import com.epam.gym.model.Trainee;
import com.epam.gym.repository.impl.TraineeRepositoryImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TraineeRepositoryTest {
    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Query<Trainee> query;

    private TraineeRepositoryImpl repository;

    @BeforeEach
    public void setUp() {
        repository = new TraineeRepositoryImpl(sessionFactory);
    }

    @Test
    public void givenTrainee_whenFindByUsername_thenReturnTrainee() {
        // Arrange
        final String username = "john.doe";
        final String parameter = "username";

        Trainee trainee = new Trainee();
        trainee.setId(UUID.randomUUID());

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(any(), eq(Trainee.class))).thenReturn(query);
        when(query.setParameter(parameter, username)).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.of(trainee));

        // Act
        Optional<Trainee> result = repository.findByUsername(username);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(trainee, result.get());

        verify(session).createQuery("from Trainee t where t.user.username = :username", Trainee.class);
        verify(query).setParameter(parameter, username);
        verify(query).uniqueResultOptional();
    }

    @Test
    public void givenNoTrainee_whenFindByUsername_thenReturnEmpty() {
        // Arrange
        final String username = "unknown.user";
        final String parameter = "username";

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(any(), eq(Trainee.class))).thenReturn(query);
        when(query.setParameter(parameter, username)).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.empty());

        // Act
        Optional<Trainee> result = repository.findByUsername(username);

        // Assert
        assertFalse(result.isPresent());

        verify(session).createQuery("from Trainee t where t.user.username = :username", Trainee.class);
        verify(query).setParameter(parameter, username);
        verify(query).uniqueResultOptional();
    }
}
