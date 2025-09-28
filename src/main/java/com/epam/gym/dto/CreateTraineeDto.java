package com.epam.gym.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateTraineeDto {
    private LocalDate birthDate;
    private String address;
    private UUID userId;
}
