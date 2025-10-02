package com.epam.gym.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "trainers")
@Data
@NoArgsConstructor
@ToString(exclude = {"trainings", "trainees"})
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "training_type_id")
    private TrainingType specialization;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Training> trainings = new ArrayList<>();

    @ManyToMany(mappedBy = "trainers")
    private List<Trainee> trainees = new ArrayList<>();
}
