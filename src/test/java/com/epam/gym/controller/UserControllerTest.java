package com.epam.gym.controller;

import com.epam.gym.dto.CreateUserDto;
import com.epam.gym.dto.CreateUserResponse;
import com.epam.gym.dto.UpdateUserDto;
import com.epam.gym.model.User;
import com.epam.gym.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private AuthController authController;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    public void givenUsername_whenGetByUsername_thenReturnUser() {
        // Arrange
        final String username = "john.doe";
        User user = new User();

        when(userService.findByUsername(username)).thenReturn(user);

        // Act
        User result = userController.getByUsername(username);

        // Assert
        assertEquals(user, result);
        verify(userService).findByUsername(username);
    }

    @Test
    public void givenCreateUserDto_whenPost_thenReturnCreateUserResponse() {
        // Arrange
        CreateUserDto createDto = new CreateUserDto();
        CreateUserResponse response = new CreateUserResponse();

        when(userService.createUser(createDto)).thenReturn(response);

        // Act
        CreateUserResponse result = userController.post(createDto);

        // Assert
        assertEquals(response, result);
        verify(userService).createUser(createDto);
    }

    @Test
    public void givenUserIdPasswordAndToken_whenPatchPassword_thenReturnUpdatedUser() {
        // Arrange
        final UUID id = UUID.randomUUID();
        final String token = "token";

        String password = "password";
        User user = new User();
        user.setUsername("john.doe");

        when(userService.updateUser(eq(id), any(UpdateUserDto.class))).thenReturn(user);

        // Act
        User result = userController.patchPassword(id, password, token);

        // Assert
        assertEquals(user, result);

        verify(authController).checkToken(token);
        verify(userService).updateUser(eq(id), any(UpdateUserDto.class));
    }

    @Test
    public void givenUserIdStatusAndToken_whenPatchStatus_thenReturnUpdatedUser() {
        // Arrange
        final UUID id = UUID.randomUUID();
        final String token = "token";
        final boolean active = false;

        User user = new User();
        user.setUsername("john.doe");

        when(userService.updateUser(eq(id), any(UpdateUserDto.class))).thenReturn(user);

        // Act
        User result = userController.patchStatus(id, active, token);

        // Assert
        assertEquals(user, result);

        verify(authController).checkToken(token);
        verify(userService).updateUser(eq(id), any(UpdateUserDto.class));
    }

    @Test
    public void givenUserIdUserDtoAndToken_whenPutUser_thenReturnUpdatedUser() {
        // Arrange
        final UUID id = UUID.randomUUID();
        final String token = "token";

        UpdateUserDto updateDto = new UpdateUserDto();
        User user = new User();

        when(userService.updateUser(id, updateDto)).thenReturn(user);

        // Act
        User result = userController.putUser(id, updateDto, token);

        // Assert
        assertEquals(user, result);

        verify(authController).checkToken(token);
        verify(userService).updateUser(id, updateDto);
    }

    @Test
    public void givenUsernameAndToken_whenDeleteByUsername_thenReturnDeletedUser() {
        // Arrange
        final String username = "john.doe";
        final String token = "token";

        User user = new User();

        when(userService.deleteUserByUsername(username)).thenReturn(user);

        // Act
        User result = userController.deleteByUsername(username, token);

        // Assert
        assertEquals(user, result);

        verify(authController).checkToken(token);
        verify(userService).deleteUserByUsername(username);
    }
}