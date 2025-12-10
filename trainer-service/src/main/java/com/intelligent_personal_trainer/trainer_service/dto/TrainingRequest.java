package com.intelligent_personal_trainer.trainer_service.dto;

import lombok.Data;

@Data
public class TrainingRequest {
    private String userId;
    private String prompt;
    private int daysHistory;
}
