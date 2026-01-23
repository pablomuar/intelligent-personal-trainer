package com.intelligent_personal_trainer.trainer_service.llm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LlmResponse(

        @JsonPropertyDescription("The normal textual response. Use it to explain, analyze or converse.")
        String chatMessage,

        @JsonPropertyDescription("STRICTLY NULL unless the user explicitly asks to CREATE/GENERATE a training plan.")
        List<PlanSessionLlm> sessions,

        @JsonPropertyDescription("CHAT_ONLY by default, PLAN_GENERATED if a training plan has been generated")
        ResponseType type
) {
    public enum ResponseType {
        CHAT_ONLY,
        PLAN_GENERATED
    }
}