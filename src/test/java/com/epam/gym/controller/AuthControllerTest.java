package com.epam.gym.controller;

import com.epam.gym.exception.AccessDeniedException;
import com.epam.gym.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    public void givenValidCredentials_whenLogin_thenReturnTokenAndTokenIsActive() {
        // Arrange
        final String username = "john";
        final String password = "secret";
        final String token = "token123";

        when(authService.login(username, password)).thenReturn(token);

        // Act
        String result = authController.login(username, password);

        // Assert
        assertEquals(token, result);
        assertDoesNotThrow(() -> authController.checkToken(token));

        verify(authService).login(username, password);
    }

    @Test
    public void givenInactiveToken_whenCheckToken_thenThrowAccessDenied() {
        // Arrange
        final String inactiveToken = "invalidToken";

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> authController.checkToken(inactiveToken));
    }
}

