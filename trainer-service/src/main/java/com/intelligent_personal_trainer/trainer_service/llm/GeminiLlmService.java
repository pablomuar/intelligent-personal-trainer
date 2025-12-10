package com.intelligent_personal_trainer.trainer_service.llm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "llm.provider", havingValue = "gemini", matchIfMissing = true)
public class GeminiLlmService implements LlmService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestClient.Builder restClientBuilder;

    @Override
    public String generateContent(String promptText) {
        log.info("Generating content using GEMINI provider...");

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", promptText)
                        ))
                )
        );

        try {
            var response = restClientBuilder.build()
                    .post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            return parseGeminiResponse(response);
        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            throw new RuntimeException("Failed to generate plan with Gemini", e);
        }
    }

    private String parseGeminiResponse(Map response) {
        // Lógica de parseo específica de Google Gemini
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        return (String) parts.get(0).get("text");
    }
}
