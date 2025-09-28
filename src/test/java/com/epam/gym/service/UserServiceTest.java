package com.epam.gym.service;

import com.epam.gym.dto.CreateUserDto;
import com.epam.gym.dto.CreateUserResponse;
import com.epam.gym.dto.UpdateUserDto;
import com.epam.gym.exception.NotFoundException;
import com.epam.gym.mapper.UserMapper;
import com.epam.gym.model.User;
import com.epam.gym.properties.AppProperties;
import com.epam.gym.repository.UserRepository;
import com.epam.gym.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static final int MAX_PASSWORD_LENGTH = 8;
    private static final String USERNAME_DELIMITER = ".";

    @Mock
    private AppProperties appProperties;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void givenStoredUser_whenFindById_thenReturnUser() {
        // Arrange
        final UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(userRepository.findByIdOrThrow(userId)).thenReturn(user);

        // Act
        User result = userService.findById(userId);

        // Assert
        assertEquals(user, result);
        verify(userRepository).findByIdOrThrow(userId);
    }

    @Test
    public void givenExistingUsername_whenFindByUsername_thenReturnUser() {
        // Arrange
        final String username = "john.doe";

        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        User result = userService.findByUsername(username);

        // Assert
        assertEquals(user, result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    public void givenNonExistingUsername_whenFindByUsername_thenThrowNotFound() {
        // Arrange
        final String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.findByUsername(username));
        verify(userRepository).findByUsername(username);
    }

    @Test
    public void givenCreateUserDto_whenCreateUser_thenReturnResponse() {
        // Arrange
        final String firstName = "John";
        final String lastName = "Doe";
        final String username = firstName + USERNAME_DELIMITER + lastName;
        final String password = "encoded";

        CreateUserDto createDto = new CreateUserDto();
        createDto.setFirstName(firstName);
        createDto.setLastName(lastName);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);

        when(userMapper.toEntity(createDto)).thenReturn(user);
        when(appProperties.getMaxPasswordLength()).thenReturn(MAX_PASSWORD_LENGTH);
        when(appProperties.getUsernameDelimiter()).thenReturn(USERNAME_DELIMITER);

        when(passwordEncoder.encode(Mockito.any(CharSequence.class))).thenReturn(password);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        CreateUserResponse response = userService.createUser(createDto);

        // Assert
        assertEquals(username, response.getUsername());
        assertNotEquals(password, response.getPassword());
        assertEquals(MAX_PASSWORD_LENGTH, response.getPassword().length());

        verify(userRepository).save(any(), eq(user));
    }

    @Test
    public void givenCreateUserDto_whenUsernameConflict_thenReturnModifiedUsername() {
        // Arrange
        final String firstName = "John";
        final String lastName = "Doe";
        final String username = firstName + USERNAME_DELIMITER + lastName;
        final String password = "encoded";
        final long occurrences = 2;

        CreateUserDto createDto = new CreateUserDto();
        createDto.setFirstName(firstName);
        createDto.setLastName(lastName);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);

        when(userMapper.toEntity(createDto)).thenReturn(user);
        when(appProperties.getMaxPasswordLength()).thenReturn(MAX_PASSWORD_LENGTH);
        when(appProperties.getUsernameDelimiter()).thenReturn(USERNAME_DELIMITER);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));
        when(userRepository.countByUsernameLike(username)).thenReturn(occurrences);

        // Act
        CreateUserResponse response = userService.createUser(createDto);

        // Assert
        final String formattedUsername = username + occurrences;

        assertEquals(formattedUsername, response.getUsername());
        assertNotEquals(password, response.getPassword());

        verify(userRepository).save(any(), eq(user));
    }

    @Test
    public void givenUpdateUser_whenUserExists_thenUpdateAndReturn() {
        // Arrange
        final UUID userId = UUID.randomUUID();

        final String newPassword = "password";
        final String encodedPassword = "encoded";

        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setPassword(newPassword);

        User user = new User();
        user.setId(userId);
        user.setPassword(encodedPassword);

        when(appProperties.getUsernameDelimiter()).thenReturn(USERNAME_DELIMITER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        // Act
        User updated = userService.updateUser(userId, updateDto);

        // Assert
        assertEquals(encodedPassword, updated.getPassword());

        verify(userMapper).updateEntityFromDto(updateDto, user);
        verify(userRepository).save(userId, user);
    }

    @Test
    public void givenUpdateUser_whenUserNotFound_thenThrowNotFound() {
        // Arrange
        final UUID userId = UUID.randomUUID();
        UpdateUserDto updateDto = new UpdateUserDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, updateDto));
        verify(userRepository).findById(userId);
    }

    @Test
    public void givenUsername_whenDeleteUserByUsername_thenReturnUser() {
        // Arrange
        final String username = "john.doe";

        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        User deleted = userService.deleteUserByUsername(username);

        // Assert
        assertEquals(user, deleted);
        verify(userRepository).delete(user.getId());
    }

    @Test
    public void givenNonExistingUsername_whenDeleteUserByUsername_thenThrowNotFound() {
        // Arrange
        final String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.deleteUserByUsername(username));
        verify(userRepository).findByUsername(username);
    }
}
