package com.epam.gym.repository;

import jakarta.persistence.criteria.CriteriaQuery;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<V, K> {
    List<V> findAll();

    List<V> findAll(CriteriaQuery<V> criteria);

    Optional<V> findById(K key);

    V findByIdOrThrow(K key);

    V save(K key, V value);

    void delete(K key);
}
