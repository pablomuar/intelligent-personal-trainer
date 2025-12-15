package com.intelligent_personal_trainer.trainer_service.llm;

import com.intelligent_personal_trainer.trainer_service.llm.dto.TrainingPlanLlmResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingPlanGeneratorService {

    private final ChatClient chatClient;

    public TrainingPlanLlmResponse generateTrainingPlan(String promptText) {
        log.info("Generating content using Spring AI 2.0 (Google GenAI)...");

        try {
            return chatClient.prompt()
                    .system("You are an expert Personal Trainer. " +
                            "You create a concise training plan based on the user's request, the user's medical conditions and the previous workouts.")
                    .user(promptText)
                    .call()
                    .entity(TrainingPlanLlmResponse.class);

        } catch (Exception e) {
            log.error("Error calling Gemini API via Spring AI", e);
            throw new RuntimeException("Failed to generate plan with Spring AI", e);
        }
    }
}