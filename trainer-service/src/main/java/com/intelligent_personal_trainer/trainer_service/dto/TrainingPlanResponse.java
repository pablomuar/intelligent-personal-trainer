package com.intelligent_personal_trainer.trainer_service.dto;

import com.intelligent_personal_trainer.trainer_service.llm.dto.TrainingPlanLlmResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainingPlanResponse {
    private String userId;
    private String originalPrompt;
    private TrainingPlanLlmResponse trainingPlan;
}
