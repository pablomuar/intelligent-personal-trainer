package com.personaltrainer.workout.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "exercises")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Exercise name is required")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Muscle group is required")
    @Column(name = "muscle_group", nullable = false)
    private String muscleGroup;

    @NotBlank(message = "Equipment is required")
    private String equipment;

    @NotNull(message = "Difficulty level is required")
    @Column(name = "difficulty_level")
    private Integer difficultyLevel; // 1-5 scale

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    // Constructors
    public Exercise() {}

    public Exercise(String name, String description, String muscleGroup, String equipment, Integer difficultyLevel) {
        this.name = name;
        this.description = description;
        this.muscleGroup = muscleGroup;
        this.equipment = equipment;
        this.difficultyLevel = difficultyLevel;
    }

    public Exercise(String name, String description, String muscleGroup, String equipment, Integer difficultyLevel, String instructions) {
        this.name = name;
        this.description = description;
        this.muscleGroup = muscleGroup;
        this.equipment = equipment;
        this.difficultyLevel = difficultyLevel;
        this.instructions = instructions;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMuscleGroup() { return muscleGroup; }
    public void setMuscleGroup(String muscleGroup) { this.muscleGroup = muscleGroup; }

    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }

    public Integer getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(Integer difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
}