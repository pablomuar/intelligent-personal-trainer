package com.intelligent_personal_trainer.trainer_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainingPlanResponse {
    private String userId;
    private String originalPrompt;
    private String trainingPlanMarkdown; // Respuesta de la IA
}
