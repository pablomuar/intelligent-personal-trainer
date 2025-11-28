package com.intelligent_personal_trainer.data_persistence_service.mapper;

import com.intelligent_personal_trainer.common.data.FitnessData;
import com.intelligent_personal_trainer.data_persistence_service.entity.FitnessDataEntity;
import org.springframework.stereotype.Component;

@Component
public class FitnessDataMapper {

    public FitnessDataEntity toEntity(FitnessData dto) {
        if (dto == null) return null;

        FitnessDataEntity entity = new FitnessDataEntity();
        entity.setTimestamp(dto.getTimestamp());
        entity.setUserId(dto.getUserId());
        entity.setAverageHeartRate(dto.getAverageHeartRate());
        entity.setTotalSteps(dto.getTotalSteps());
        entity.setTotalDistance(dto.getTotalDistance());
        entity.setTotalCaloriesBurned(dto.getTotalCaloriesBurned());
        entity.setWorkoutDataList(dto.getWorkoutDataList());

        return entity;
    }

    public FitnessData toDto(FitnessDataEntity entity) {
        if (entity == null)
            return null;

        return FitnessData.builder()
                .timestamp(entity.getTimestamp())
                .userId(entity.getUserId())
                .averageHeartRate(entity.getAverageHeartRate())
                .totalSteps(entity.getTotalSteps())
                .totalDistance(entity.getTotalDistance())
                .totalCaloriesBurned(entity.getTotalCaloriesBurned())
                .workoutDataList(entity.getWorkoutDataList())
                .build();
    }
}
