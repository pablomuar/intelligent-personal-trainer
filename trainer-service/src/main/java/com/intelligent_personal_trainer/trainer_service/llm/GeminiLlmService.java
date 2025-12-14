package com.intelligent_personal_trainer.trainer_service.llm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "llm.provider", havingValue = "gemini", matchIfMissing = true)
public class GeminiLlmService implements LlmService {

    private final ChatClient chatClient;

    @Override
    public String generateContent(String promptText) {
        log.info("Generating content using Spring AI 2.0 (Google GenAI)...");

        try {
            return chatClient.prompt()
                    .user(promptText)
                    .call()
                    .content();

        } catch (Exception e) {
            log.error("Error calling Gemini API via Spring AI", e);
            throw new RuntimeException("Failed to generate plan with Spring AI", e);
        }
    }
}