package com.epam.gym.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class UpdateTraineeDto {
    private LocalDate birthDate;
    private String address;
    private UUID userId;
    private List<UUID> trainerIds;
}
