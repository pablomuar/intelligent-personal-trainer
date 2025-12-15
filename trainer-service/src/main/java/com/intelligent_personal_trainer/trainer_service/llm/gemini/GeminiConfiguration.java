package com.intelligent_personal_trainer.trainer_service.llm.gemini;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "llm.provider", havingValue = "gemini", matchIfMissing = true)
class GeminiConfiguration {

    @Value("${gemini.model}")
    private String geminiModel;

    @Value("${gemini.temperature:0.7}")
    private Double geminiTemperature;

    @Value("${gemini.top_p:1.0}")
    private Double geminiTopP;

    @Bean
    public ChatClient geminiChatClient(ChatClient.Builder chatClientBuilder) {
        GoogleGenAiChatOptions googleGenAiChatOptions = GoogleGenAiChatOptions.builder()
                .temperature(geminiTemperature)
                .topP(geminiTopP)
                .model(geminiModel)
                .build();

        return chatClientBuilder
                .defaultOptions(googleGenAiChatOptions)
                .build();
    }
}
