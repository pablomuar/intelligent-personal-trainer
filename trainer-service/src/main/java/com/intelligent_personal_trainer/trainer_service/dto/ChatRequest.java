package com.intelligent_personal_trainer.trainer_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequest {
    private String userId;
    private Long conversationId;
    private String prompt;
    private String userLocation;
}
