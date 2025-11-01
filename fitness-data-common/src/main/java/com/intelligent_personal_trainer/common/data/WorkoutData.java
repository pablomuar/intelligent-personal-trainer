package com.intelligent_personal_trainer.common.data;

import lombok.Data;

@Data
public class WorkoutData {
    private String workoutType;
    private int durationMinutes;
    private double distanceKm;
    private double caloriesBurned;
}
