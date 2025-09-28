package com.epam.gym.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class UpdateTrainingDto {
    private String name;
    private LocalDate date;
    private int duration;
    private UUID trainingTypeId;
    private UUID traineeId;
    private UUID trainerId;
}
