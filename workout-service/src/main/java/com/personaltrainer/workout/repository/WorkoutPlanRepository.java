package com.personaltrainer.workout.repository;

import com.personaltrainer.workout.entity.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
    List<WorkoutPlan> findByUserId(Long userId);
    List<WorkoutPlan> findByFitnessGoal(String fitnessGoal);
    List<WorkoutPlan> findByDifficultyLevel(Integer difficultyLevel);
}