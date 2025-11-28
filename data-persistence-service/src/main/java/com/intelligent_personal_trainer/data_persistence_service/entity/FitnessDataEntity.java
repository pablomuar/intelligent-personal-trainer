package com.intelligent_personal_trainer.data_persistence_service.entity;

import com.intelligent_personal_trainer.common.data.WorkoutData;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "fitness_data")
@Data
public class FitnessDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private String userId;

    private double averageHeartRate;
    private int totalSteps;
    private double totalDistance;
    private double totalCaloriesBurned;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "workout_list", columnDefinition = "jsonb")
    private List<WorkoutData> workoutDataList;
}
