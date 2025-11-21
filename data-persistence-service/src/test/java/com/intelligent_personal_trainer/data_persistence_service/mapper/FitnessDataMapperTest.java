package com.intelligent_personal_trainer.data_persistence_service.mapper;

import com.intelligent_personal_trainer.common.data.FitnessData;
import com.intelligent_personal_trainer.common.data.WorkoutData;
import com.intelligent_personal_trainer.data_persistence_service.entity.FitnessDataEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FitnessDataMapperTest {

    private FitnessDataMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new FitnessDataMapper();
    }

    @Test
    void toEntity_shouldMapDtoToEntityCorrectly() {
        WorkoutData workout = WorkoutData.builder()
                .workoutType("Running")
                .durationMinutes(30)
                .distanceKm(5.0)
                .caloriesBurned(300.0)
                .build();

        List<WorkoutData> workoutList = Collections.singletonList(workout);

        FitnessData dto = FitnessData.builder()
                .userId("user123")
                .averageHeartRate(80.0)
                .totalSteps(5000)
                .totalDistance(4.0)
                .totalCaloriesBurned(400.0)
                .workoutDataList(workoutList)
                .build();

        FitnessDataEntity entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getUserId(), entity.getUserId());
        assertEquals(dto.getAverageHeartRate(), entity.getAverageHeartRate());
        assertEquals(dto.getTotalSteps(), entity.getTotalSteps());
        assertEquals(dto.getTotalDistance(), entity.getTotalDistance());
        assertEquals(dto.getTotalCaloriesBurned(), entity.getTotalCaloriesBurned());
        assertEquals(dto.getWorkoutDataList(), entity.getWorkoutDataList());
    }

    @Test
    void toEntity_shouldReturnNullWhenDtoIsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void toDto_shouldMapEntityToDtoCorrectly() {
        WorkoutData workout = WorkoutData.builder()
                .workoutType("Cycling")
                .durationMinutes(45)
                .distanceKm(15.0)
                .caloriesBurned(500.0)
                .build();

        List<WorkoutData> workoutList = Collections.singletonList(workout);

        FitnessDataEntity entity = new FitnessDataEntity();
        entity.setUserId("user456");
        entity.setAverageHeartRate(90.0);
        entity.setTotalSteps(1000);
        entity.setTotalDistance(15.0);
        entity.setTotalCaloriesBurned(500.0);
        entity.setWorkoutDataList(workoutList);

        FitnessData dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(entity.getUserId(), dto.getUserId());
        assertEquals(entity.getAverageHeartRate(), dto.getAverageHeartRate());
        assertEquals(entity.getTotalSteps(), dto.getTotalSteps());
        assertEquals(entity.getTotalDistance(), dto.getTotalDistance());
        assertEquals(entity.getTotalCaloriesBurned(), dto.getTotalCaloriesBurned());
        assertEquals(entity.getWorkoutDataList(), dto.getWorkoutDataList());
    }

    @Test
    void toDto_shouldReturnNullWhenEntityIsNull() {
        assertNull(mapper.toDto(null));
    }
}
