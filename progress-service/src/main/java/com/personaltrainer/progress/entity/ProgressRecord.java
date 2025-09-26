package com.personaltrainer.progress.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress_records")
public class ProgressRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Workout plan ID is required")
    @Column(name = "workout_plan_id", nullable = false)
    private Long workoutPlanId;

    @NotNull(message = "Exercise ID is required")
    @Column(name = "exercise_id", nullable = false)
    private Long exerciseId;

    @NotNull(message = "Sets completed is required")
    @Column(name = "sets_completed")
    private Integer setsCompleted;

    @NotNull(message = "Reps completed is required")
    @Column(name = "reps_completed")
    private Integer repsCompleted;

    @Column(name = "weight_used")
    private Integer weightUsed; // in kg

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "calories_burned")
    private Integer caloriesBurned;

    private String notes;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }

    // Constructors
    public ProgressRecord() {}

    public ProgressRecord(Long userId, Long workoutPlanId, Long exerciseId, Integer setsCompleted, Integer repsCompleted) {
        this.userId = userId;
        this.workoutPlanId = workoutPlanId;
        this.exerciseId = exerciseId;
        this.setsCompleted = setsCompleted;
        this.repsCompleted = repsCompleted;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getWorkoutPlanId() { return workoutPlanId; }
    public void setWorkoutPlanId(Long workoutPlanId) { this.workoutPlanId = workoutPlanId; }

    public Long getExerciseId() { return exerciseId; }
    public void setExerciseId(Long exerciseId) { this.exerciseId = exerciseId; }

    public Integer getSetsCompleted() { return setsCompleted; }
    public void setSetsCompleted(Integer setsCompleted) { this.setsCompleted = setsCompleted; }

    public Integer getRepsCompleted() { return repsCompleted; }
    public void setRepsCompleted(Integer repsCompleted) { this.repsCompleted = repsCompleted; }

    public Integer getWeightUsed() { return weightUsed; }
    public void setWeightUsed(Integer weightUsed) { this.weightUsed = weightUsed; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(Integer caloriesBurned) { this.caloriesBurned = caloriesBurned; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}