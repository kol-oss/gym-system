package com.epam.gym.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@FieldNameConstants
@ToString(exclude = {"trainee", "trainer"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean isActive;

    @OneToOne(cascade = CascadeType.ALL)
    private Trainee trainee;

    @OneToOne(cascade = CascadeType.ALL)
    private Trainer trainer;
}
