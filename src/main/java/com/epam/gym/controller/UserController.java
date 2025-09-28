package com.epam.gym.controller;

import com.epam.gym.dto.CreateUserDto;
import com.epam.gym.dto.CreateUserResponse;
import com.epam.gym.dto.UpdateUserDto;
import com.epam.gym.model.User;
import com.epam.gym.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class UserController {
    private AuthController authController;
    private UserService userService;

    public User getByUsername(String username) {
        return userService.findByUsername(username);
    }

    public CreateUserResponse post(CreateUserDto userDto) {
        return userService.createUser(userDto);
    }

    public User patchPassword(UUID userId, String password, String token) {
        authController.checkToken(token);

        UpdateUserDto userDto = new UpdateUserDto();
        userDto.setPassword(password);

        return userService.updateUser(userId, userDto);
    }

    public User patchStatus(UUID userId, boolean isActive, String token) {
        authController.checkToken(token);

        UpdateUserDto userDto = new UpdateUserDto();
        userDto.setActive(isActive);

        return userService.updateUser(userId, userDto);
    }

    public User putUser(UUID userId, UpdateUserDto userDto, String token) {
        authController.checkToken(token);

        return userService.updateUser(userId, userDto);
    }

    public User deleteByUsername(String username, String token) {
        authController.checkToken(token);

        return userService.deleteUserByUsername(username);
    }
}
