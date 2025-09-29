package com.epam.gym.repository;

import com.epam.gym.model.User;
import com.epam.gym.repository.impl.UserRepositoryImpl;
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
public class UserRepositoryTest {
    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Query<User> userQuery;

    @Mock
    private Query<Long> countQuery;

    private UserRepositoryImpl repository;

    @BeforeEach
    public void setUp() {
        repository = new UserRepositoryImpl(sessionFactory);
    }

    @Test
    public void givenUser_whenFindByUsername_thenReturnUser() {
        // Arrange
        final String username = "john_doe";
        final String parameter = "username";

        User user = new User();
        user.setId(UUID.randomUUID());

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(any(), eq(User.class))).thenReturn(userQuery);
        when(userQuery.setParameter(parameter, username)).thenReturn(userQuery);
        when(userQuery.uniqueResultOptional()).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = repository.findByUsername(username);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user, result.get());

        verify(session).createQuery("from User u where u.username = :username", User.class);
        verify(userQuery).setParameter(parameter, username);
        verify(userQuery).uniqueResultOptional();
    }

    @Test
    public void givenNoUser_whenFindByUsername_thenReturnEmpty() {
        // Arrange
        final String username = "unknown_user";
        final String parameter = "username";

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(any(), eq(User.class))).thenReturn(userQuery);
        when(userQuery.setParameter(parameter, username)).thenReturn(userQuery);
        when(userQuery.uniqueResultOptional()).thenReturn(Optional.empty());

        // Act
        Optional<User> result = repository.findByUsername(username);

        // Assert
        assertFalse(result.isPresent());

        verify(session).createQuery("from User u where u.username = :username", User.class);
        verify(userQuery).setParameter(parameter, username);
        verify(userQuery).uniqueResultOptional();
    }

    @Test
    public void givenUsers_whenCountByUsernameLike_thenReturnCount() {
        // Arrange
        final String username = "john";
        final String parameter = "pattern";

        final Long expectedCount = 5L;
        final String pattern = username + "%";

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(any(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter(parameter, pattern)).thenReturn(countQuery);
        when(countQuery.uniqueResult()).thenReturn(expectedCount);

        // Act
        long result = repository.countByUsernameLike(username);

        // Assert
        assertEquals(expectedCount, result);

        verify(session).createQuery("select count(u) from User u where u.username like :pattern", Long.class);
        verify(countQuery).setParameter(parameter, pattern);
        verify(countQuery).uniqueResult();
    }

    @Test
    public void givenNoUsers_whenCountByUsernameLike_thenReturnZero() {
        // Arrange
        final String username = "unknown_user";
        final String parameter = "pattern";
        final String pattern = username + "%";

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(any(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter(parameter, pattern)).thenReturn(countQuery);
        when(countQuery.uniqueResult()).thenReturn(null);

        // Act
        long result = repository.countByUsernameLike(username);

        // Assert
        assertEquals(0, result);

        verify(session).createQuery("select count(u) from User u where u.username like :pattern", Long.class);
        verify(countQuery).setParameter(parameter, pattern);
        verify(countQuery).uniqueResult();
    }
}
