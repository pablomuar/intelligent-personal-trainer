package com.intelligent_personal_trainer.trainer_service.llm.dto;

public record PlanSessionLlm (
        String day,
        int duration,
        IntensityLevel intensity,
        String sessionDescription
) {

    enum IntensityLevel {
        REST,
        LOW,
        LOW_MEDIUM,
        MEDIUM,
        MEDIUM_HIGH,
        HIGH
    }
}
