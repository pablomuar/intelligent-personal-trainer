package com.intelligent_personal_trainer.trainer_service.llm.dto;

import java.util.List;

public record TrainingPlanLlmResponse (
        String recommendation,
        String analysis,
        List<PlanSessionLlm> sessions,
        ConfidenceLevel confidence
) {

    enum ConfidenceLevel {
        HIGH,
        MEDIUM,
        LOW
    }
}

