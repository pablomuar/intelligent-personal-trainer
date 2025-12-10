package com.intelligent_personal_trainer.trainer_service.llm;

public interface LlmService {

    /**
     * Sends a prompt to the configured AI provider and returns the response as plain text.
     * @param prompt The text with the context and the request.
     * @return The generated training plan or response.
     */
    String generateContent(String prompt);
}
