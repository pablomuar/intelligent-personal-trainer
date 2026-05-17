package com.intelligent_personal_trainer.trainer_service.dto;

import com.intelligent_personal_trainer.trainer_service.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {
    private Long id;
    private ChatMessage.Role role;
    private String content;
    private Instant createdAt;
}
