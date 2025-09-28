package com.epam.gym.repository.impl;

import com.epam.gym.exception.NotFoundException;
import com.epam.gym.repository.CrudRepository;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class CrudRepositoryImpl<V, K> implements CrudRepository<V, K> {
    protected final SessionFactory sessionFactory;
    protected final Class<V> valueClazz;

    protected Session getReadSession() {
        Session session = sessionFactory.openSession();
        session.setDefaultReadOnly(true);
        return session;
    }

    protected Session getTransactionalSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<V> findAll() {
        try (Session session = getReadSession()) {
            List<V> result = session
                    .createQuery("from " + valueClazz.getSimpleName(), valueClazz)
                    .getResultList();

            log.debug("Found {} records for find all {} request", result.size(), valueClazz.getSimpleName());
            return result;
        }
    }

    @Override
    public List<V> findAll(CriteriaQuery<V> criteria) {
        try (Session session = getReadSession()) {
            List<V> result = session
                    .createQuery(criteria)
                    .getResultList();

            log.debug("Filtered {} records for find all {} with criteria request", result.size(), valueClazz.getSimpleName());
            return result;
        }
    }

    @Override
    public Optional<V> findById(K key) {
        try (Session session = getReadSession()) {
            V result = session.find(valueClazz, key);

            log.debug("Returned {} record for id {}", valueClazz.getSimpleName(), key);
            return Optional.ofNullable(result);
        }
    }

    @Override
    public V findByIdOrThrow(K key) {
        return findById(key)
                .orElseThrow(() -> new NotFoundException(valueClazz.getSimpleName() + " not found via id " + key));
    }

    @Override
    public V save(K key, V value) {
        Session session = getTransactionalSession();
        V existing = key != null ? session.find(valueClazz, key) : null;

        if (existing != null) {
            session.merge(value);
            log.debug("Updated {} record for id {}", valueClazz.getSimpleName(), value);
        } else {
            session.persist(value);
            log.debug("Created new {} record", valueClazz.getSimpleName());
        }

        return value;
    }

    @Override
    public void delete(K key) {
        Session session = getTransactionalSession();
        V value = session.find(valueClazz, key);

        if (value != null) {
            session.remove(value);
            log.debug("Deleted {} record for id {}", valueClazz.getSimpleName(), key);
        } else {
            log.debug("No records delete for {} request for id {}", valueClazz.getSimpleName(), key);
        }
    }
}
