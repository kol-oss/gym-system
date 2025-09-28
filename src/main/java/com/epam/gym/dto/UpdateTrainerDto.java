package com.epam.gym.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UpdateTrainerDto {
    private UUID specializationId;
    private UUID userId;
    private List<UUID> traineeIds;
}
