package com.epam.gym.repository.impl;

import com.epam.gym.model.User;
import com.epam.gym.repository.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl extends CrudRepositoryImpl<User, UUID> implements UserRepository {
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, User.class);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from User u where u.username = :username", User.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    @Override
    public long countByUsernameLike(String username) {
        Session session = sessionFactory.getCurrentSession();
        String pattern = username + "%";

        Long count = session.createQuery(
                        "select count(u) from User u where u.username like :pattern",
                        Long.class
                )
                .setParameter("pattern", pattern)
                .uniqueResult();

        return count != null ? count : 0;
    }
}
