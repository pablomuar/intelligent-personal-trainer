package com.personaltrainer.workout.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "workout_sessions")
public class WorkoutSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id")
    private WorkoutPlan workoutPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @NotNull(message = "Sets is required")
    private Integer sets;

    @NotNull(message = "Reps is required")
    private Integer reps;

    private Integer weight; // in kg

    @Column(name = "rest_time_seconds")
    private Integer restTimeSeconds;

    private String notes;

    @Column(name = "session_order")
    private Integer sessionOrder;

    // Constructors
    public WorkoutSession() {}

    public WorkoutSession(WorkoutPlan workoutPlan, Exercise exercise, Integer sets, Integer reps) {
        this.workoutPlan = workoutPlan;
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public WorkoutPlan getWorkoutPlan() { return workoutPlan; }
    public void setWorkoutPlan(WorkoutPlan workoutPlan) { this.workoutPlan = workoutPlan; }

    public Exercise getExercise() { return exercise; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }

    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }

    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }

    public Integer getWeight() { return weight; }
    public void setWeight(Integer weight) { this.weight = weight; }

    public Integer getRestTimeSeconds() { return restTimeSeconds; }
    public void setRestTimeSeconds(Integer restTimeSeconds) { this.restTimeSeconds = restTimeSeconds; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getSessionOrder() { return sessionOrder; }
    public void setSessionOrder(Integer sessionOrder) { this.sessionOrder = sessionOrder; }
}