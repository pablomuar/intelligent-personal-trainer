package com.personaltrainer.workout.controller;

import com.personaltrainer.workout.entity.Exercise;
import com.personaltrainer.workout.entity.WorkoutPlan;
import com.personaltrainer.workout.repository.ExerciseRepository;
import com.personaltrainer.workout.repository.WorkoutPlanRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@CrossOrigin(origins = "*")
public class WorkoutController {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    // Exercise endpoints
    @GetMapping("/exercises")
    public ResponseEntity<List<Exercise>> getAllExercises() {
        List<Exercise> exercises = exerciseRepository.findAll();
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/exercises/{id}")
    public ResponseEntity<Exercise> getExerciseById(@PathVariable Long id) {
        return exerciseRepository.findById(id)
                .map(exercise -> ResponseEntity.ok(exercise))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/exercises/muscle-group/{muscleGroup}")
    public ResponseEntity<List<Exercise>> getExercisesByMuscleGroup(@PathVariable String muscleGroup) {
        List<Exercise> exercises = exerciseRepository.findByMuscleGroup(muscleGroup);
        return ResponseEntity.ok(exercises);
    }

    @PostMapping("/exercises")
    public ResponseEntity<Exercise> createExercise(@Valid @RequestBody Exercise exercise) {
        Exercise savedExercise = exerciseRepository.save(exercise);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExercise);
    }

    // Workout Plan endpoints
    @GetMapping("/plans")
    public ResponseEntity<List<WorkoutPlan>> getAllWorkoutPlans() {
        List<WorkoutPlan> plans = workoutPlanRepository.findAll();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/plans/{id}")
    public ResponseEntity<WorkoutPlan> getWorkoutPlanById(@PathVariable Long id) {
        return workoutPlanRepository.findById(id)
                .map(plan -> ResponseEntity.ok(plan))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/plans/user/{userId}")
    public ResponseEntity<List<WorkoutPlan>> getWorkoutPlansByUserId(@PathVariable Long userId) {
        List<WorkoutPlan> plans = workoutPlanRepository.findByUserId(userId);
        return ResponseEntity.ok(plans);
    }

    @PostMapping("/plans")
    public ResponseEntity<WorkoutPlan> createWorkoutPlan(@Valid @RequestBody WorkoutPlan workoutPlan) {
        WorkoutPlan savedPlan = workoutPlanRepository.save(workoutPlan);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPlan);
    }

    @PutMapping("/plans/{id}")
    public ResponseEntity<WorkoutPlan> updateWorkoutPlan(@PathVariable Long id, @Valid @RequestBody WorkoutPlan planDetails) {
        return workoutPlanRepository.findById(id)
                .map(plan -> {
                    plan.setName(planDetails.getName());
                    plan.setDescription(planDetails.getDescription());
                    plan.setDurationWeeks(planDetails.getDurationWeeks());
                    plan.setTargetMuscleGroups(planDetails.getTargetMuscleGroups());
                    plan.setFitnessGoal(planDetails.getFitnessGoal());
                    plan.setDifficultyLevel(planDetails.getDifficultyLevel());
                    return ResponseEntity.ok(workoutPlanRepository.save(plan));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/plans/{id}")
    public ResponseEntity<?> deleteWorkoutPlan(@PathVariable Long id) {
        return workoutPlanRepository.findById(id)
                .map(plan -> {
                    workoutPlanRepository.delete(plan);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Workout Service is running!");
    }
}