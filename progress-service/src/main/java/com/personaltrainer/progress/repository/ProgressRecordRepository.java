package com.personaltrainer.progress.repository;

import com.personaltrainer.progress.entity.ProgressRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProgressRecordRepository extends JpaRepository<ProgressRecord, Long> {
    List<ProgressRecord> findByUserId(Long userId);
    List<ProgressRecord> findByUserIdAndWorkoutPlanId(Long userId, Long workoutPlanId);
    List<ProgressRecord> findByUserIdAndExerciseId(Long userId, Long exerciseId);
    List<ProgressRecord> findByUserIdAndCompletedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT SUM(p.caloriesBurned) FROM ProgressRecord p WHERE p.userId = :userId AND p.completedAt BETWEEN :start AND :end")
    Integer getTotalCaloriesBurnedByUserAndDateRange(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(p) FROM ProgressRecord p WHERE p.userId = :userId AND p.completedAt BETWEEN :start AND :end")
    Long getWorkoutCountByUserAndDateRange(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}