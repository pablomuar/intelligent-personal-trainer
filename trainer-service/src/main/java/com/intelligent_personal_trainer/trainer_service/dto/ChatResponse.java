package com.intelligent_personal_trainer.trainer_service.dto;

import com.intelligent_personal_trainer.trainer_service.llm.dto.LlmResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
    private Long conversationId;
    private String title;
    private LlmResponse response;
}
