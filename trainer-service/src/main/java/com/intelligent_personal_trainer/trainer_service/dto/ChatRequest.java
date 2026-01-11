package com.intelligent_personal_trainer.trainer_service.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private String userId;
    private String prompt;
}
