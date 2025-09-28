package com.epam.gym.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateTrainerDto {
    private UUID specializationId;
    private UUID userId;
}
