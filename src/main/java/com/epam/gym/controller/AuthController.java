package com.epam.gym.controller;

import com.epam.gym.exception.AccessDeniedException;
import com.epam.gym.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class AuthController {
    private static final Set<String> activeTokens = ConcurrentHashMap.newKeySet();

    private AuthService authService;

    public String login(String username, String password) {
        String token = authService.login(username, password);
        activeTokens.add(token);

        return token;
    }

    public void checkToken(String token) {
        if (!activeTokens.contains(token)) {
            throw new AccessDeniedException("Token " + token + " is not active");
        }
    }
}
