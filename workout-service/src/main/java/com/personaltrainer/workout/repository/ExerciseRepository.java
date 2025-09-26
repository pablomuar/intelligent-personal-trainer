package com.personaltrainer.workout.repository;

import com.personaltrainer.workout.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByMuscleGroup(String muscleGroup);
    List<Exercise> findByEquipment(String equipment);
    List<Exercise> findByDifficultyLevel(Integer difficultyLevel);
    List<Exercise> findByMuscleGroupAndDifficultyLevel(String muscleGroup, Integer difficultyLevel);
}