package com.epam.gym.service.impl;

import com.epam.gym.dto.CreateUserDto;
import com.epam.gym.dto.CreateUserResponse;
import com.epam.gym.dto.UpdateUserDto;
import com.epam.gym.exception.NotFoundException;
import com.epam.gym.mapper.UserMapper;
import com.epam.gym.model.User;
import com.epam.gym.properties.AppProperties;
import com.epam.gym.repository.UserRepository;
import com.epam.gym.service.UserService;
import com.epam.gym.utils.PasswordUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private AppProperties appProperties;
    private PasswordEncoder passwordEncoder;

    private UserRepository userRepository;
    private UserMapper userMapper;

    private String createUsername(String firstName, String lastName) {
        String username = firstName + appProperties.getUsernameDelimiter() + lastName;
        if (userRepository.findByUsername(username).isPresent()) {
            long occurrences = userRepository.countByUsernameLike(username);

            String conflicted = username;
            username += occurrences;

            log.debug("Username {} was changed into {} due to conflict with existing one", conflicted, username);
        }

        return username;
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findByIdOrThrow(id);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with username " + username + " not found"));
    }

    @Override
    @Transactional
    public CreateUserResponse createUser(CreateUserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user.setActive(true);

        // Setting password
        String password = PasswordUtils.generate(appProperties.getMaxPasswordLength());
        user.setPassword(passwordEncoder.encode(password));

        // Setting username
        String username = createUsername(user.getFirstName(), user.getLastName());
        user.setUsername(username);

        userRepository.save(user.getId(), user);
        log.debug("User with name {} created", username);

        CreateUserResponse response = userMapper.toResponse(user);
        response.setPassword(password);

        return response;
    }

    @Override
    @Transactional
    public User updateUser(UUID id, UpdateUserDto userDto) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        userMapper.updateEntityFromDto(userDto, user);

        // Setting username
        String username = createUsername(user.getFirstName(), user.getLastName());

        user.setUsername(username);
        if (userDto.getPassword() != null)
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        userRepository.save(user.getId(), user);

        log.debug("User {} change validation passed", username);
        return user;
    }

    @Override
    @Transactional
    public User deleteUserByUsername(String username) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with username " + username + " not found"));

        userRepository.delete(user.getId());
        return user;
    }
}