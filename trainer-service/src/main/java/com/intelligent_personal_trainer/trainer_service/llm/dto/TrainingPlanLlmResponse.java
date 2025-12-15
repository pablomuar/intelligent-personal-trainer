package com.intelligent_personal_trainer.trainer_service.llm.dto;

import java.util.List;

public record TrainingPlanLlmResponse (
        String summary,
        List<PlanSessionLlm> sessions,
        ConfidenceLevel confidence
) {

    enum ConfidenceLevel {
        HIGH,
        MEDIUM,
        LOW
    }
}

