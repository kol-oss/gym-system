package com.epam.gym.dto;

import lombok.Data;

@Data
public class UpdateUserDto {
    private String firstName;
    private String lastName;
    private String password;
    private Boolean isActive;
}
