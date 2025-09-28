package com.epam.gym.service.impl;

import com.epam.gym.exception.AccessDeniedException;
import com.epam.gym.model.User;
import com.epam.gym.repository.UserRepository;
import com.epam.gym.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public String login(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new AccessDeniedException("Access denied: User with name " + username + " not found");
        }

        boolean matches = passwordEncoder.matches(password, user.get().getPassword());
        if (!matches) {
            throw new AccessDeniedException("Access denied: provided password is incorrect");
        }

        if (!user.get().isActive()) {
            throw new AccessDeniedException("Access denied: user is not active");
        }

        return UUID.randomUUID().toString();
    }
}
