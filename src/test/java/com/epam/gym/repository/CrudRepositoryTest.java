package com.epam.gym.repository;

import com.epam.gym.exception.NotFoundException;
import com.epam.gym.repository.impl.CrudRepositoryImpl;
import jakarta.persistence.criteria.CriteriaQuery;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CrudRepositoryTest {
    @Mock
    private Query<String> query;

    @Mock
    private CriteriaQuery<String> criteriaQuery;

    @Mock
    private Session session;

    @Mock
    private SessionFactory sessionFactory;

    private CrudRepositoryImpl<String, UUID> repository;

    @BeforeEach
    public void setUp() {
        repository = new CrudRepositoryImpl<>(sessionFactory, String.class);
    }

    @Test
    public void givenStoredRecords_whenFindAll_thenReturnRecords() {
        // Arrange
        final List<String> stored = List.of("Hello", "World");

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(any(), eq(String.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(stored);

        // Act
        List<String> result = repository.findAll();

        // Assert
        assertEquals(stored, result);

        verify(session).createQuery(eq("from String"), eq(String.class));
        verify(query).getResultList();
    }

    @Test
    public void givenStoredRecords_whenFindAllWithCriteria_thenReturnRecords() {
        // Arrange
        final List<String> stored = List.of("Hello");
        final CriteriaQuery<String> criteria = criteriaQuery;

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.createQuery(criteria)).thenReturn(query);
        when(query.getResultList()).thenReturn(stored);

        // Act
        List<String> result = repository.findAll(criteria);

        // Assert
        assertEquals(stored, result);

        verify(session).createQuery(eq(criteria));
        verify(query).getResultList();
    }

    @Test
    public void givenStoredRecord_whenFindById_thenReturnRecord() {
        // Arrange
        final UUID id = UUID.randomUUID();
        final String stored = "Hello, World!";

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.find(String.class, id)).thenReturn(stored);

        // Act
        Optional<String> result = repository.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(stored, result.get());

        verify(session).find(eq(String.class), eq(id));
    }

    @Test
    public void givenNotStoredRecord_whenFindById_thenReturnEmpty() {
        // Arrange
        final UUID id = UUID.randomUUID();

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.find(String.class, id)).thenReturn(null);

        // Act
        Optional<String> result = repository.findById(id);

        // Assert
        assertFalse(result.isPresent());

        verify(session).find(eq(String.class), eq(id));
    }

    @Test
    public void givenNotStoredRecord_whenFindByIdOrThrow_thenThrowNotFound() {
        // Arrange
        final UUID id = UUID.randomUUID();

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.find(String.class, id)).thenReturn(null);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> repository.findByIdOrThrow(id));
        verify(session).find(eq(String.class), eq(id));
    }

    @Test
    public void givenNotStoredRecord_whenSave_thenPersistAndReturn() {
        // Arrange
        final UUID id = UUID.randomUUID();
        final String value = "Hello, World!";

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.find(String.class, id)).thenReturn(null);

        // Act
        String result = repository.save(id, value);

        // Assert
        assertEquals(value, result);

        verify(session).find(eq(String.class), eq(id));
        verify(session).persist(eq(value));
        verify(session, never()).merge(any());
    }

    @Test
    public void givenStoredRecord_whenSave_thenMergeAndReturn() {
        // Arrange
        final UUID id = UUID.randomUUID();
        final String stored = "Hello, World!";

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.find(String.class, id)).thenReturn(stored);

        // Act
        String result = repository.save(id, stored);

        // Assert
        assertEquals(stored, result);

        verify(session).find(eq(String.class), eq(id));
        verify(session).merge(eq(stored));
        verify(session, never()).persist(any());
    }


    @Test
    public void givenStoredRecord_whenDelete_thenRemoveAndReturn() {
        // Arrange
        final UUID id = UUID.randomUUID();
        final String stored = "Hello, World!";

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.find(String.class, id)).thenReturn(stored);

        // Act
        repository.delete(id);

        // Assert
        verify(session).find(eq(String.class), eq(id));
        verify(session).remove(eq(stored));
    }

    @Test
    public void givenNotStoredRecord_whenDelete_thenReturnEmpty() {
        // Arrange
        final UUID id = UUID.randomUUID();

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.find(String.class, id)).thenReturn(null);

        // Act
        repository.delete(id);

        // Assert
        verify(session).find(eq(String.class), eq(id));
        verify(session, never()).remove(any());
    }
}
