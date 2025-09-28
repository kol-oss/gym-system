package com.epam.gym.service;

import com.epam.gym.dto.CreateUserDto;
import com.epam.gym.dto.CreateUserResponse;
import com.epam.gym.dto.UpdateUserDto;
import com.epam.gym.model.User;

import java.util.UUID;

public interface UserService {
    User findById(UUID id);

    User findByUsername(String username);

    CreateUserResponse createUser(CreateUserDto user);

    User updateUser(UUID id, UpdateUserDto user);

    User deleteUserByUsername(String username);
}
