package com.intelligent_personal_trainer.trainer_service.llm.dto;

public record PlanSessionLlm (
        String day,
        int duration,
        String intensity,
        String sessionDescription
) { }
