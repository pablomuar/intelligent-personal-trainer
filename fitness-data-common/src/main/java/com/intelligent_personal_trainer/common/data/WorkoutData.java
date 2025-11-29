package com.intelligent_personal_trainer.common.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutData {
    private String workoutType;
    private int durationMinutes;
    private double distanceKm;
    private double caloriesBurned;
}
