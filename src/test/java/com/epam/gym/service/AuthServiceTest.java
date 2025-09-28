package com.epam.gym.service;

import com.epam.gym.exception.AccessDeniedException;
import com.epam.gym.model.User;
import com.epam.gym.repository.UserRepository;
import com.epam.gym.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    public void givenStoredUser_whenLogin_thenReturnToken() {
        // Arrange
        final String username = "admin";
        final String password = "12345";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, password)).thenReturn(true);

        // Act
        String token = authService.login(username, password);

        // Assert
        assertNotNull(token);
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(password, password);
    }

    @Test
    public void givenNotStoredUser_whenLogin_thenThrowAccessDenied() {
        // Arrange
        final String username = "admin";
        final String password = "12345";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> authService.login(username, password));

        verify(userRepository).findByUsername(username);
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    public void givenStoredUserWithWrongPassword_whenLogin_thenThrowAccessDenied() {
        // Arrange
        final String username = "admin";
        final String password = "12345";
        final String wrongPassword = "54321";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(wrongPassword, password)).thenReturn(false);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> authService.login(username, wrongPassword));

        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(wrongPassword, password);
    }

    @Test
    public void givenStoredUserNotActive_whenLogin_thenThrowAccessDenied() {
        // Arrange
        final String username = "admin";
        final String password = "12345";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(false);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, password)).thenReturn(true);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> authService.login(username, password));

        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(password, password);
    }
}

