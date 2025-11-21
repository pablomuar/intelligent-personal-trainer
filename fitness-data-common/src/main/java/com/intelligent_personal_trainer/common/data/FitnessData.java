package com.intelligent_personal_trainer.common.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FitnessData {
    private String userId;
    private double averageHeartRate;
    private int totalSteps;
    private double totalDistance;
    private double totalCaloriesBurned;
    private List<WorkoutData> workoutDataList;
}
