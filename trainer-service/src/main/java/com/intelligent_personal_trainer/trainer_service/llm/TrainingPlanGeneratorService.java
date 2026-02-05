package com.intelligent_personal_trainer.trainer_service.llm;

import com.intelligent_personal_trainer.common.dto.RagDocumentResponse;
import com.intelligent_personal_trainer.trainer_service.client.RagServiceClient;
import com.intelligent_personal_trainer.trainer_service.llm.dto.TrainingPlanLlmResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingPlanGeneratorService {

    private final ChatClient chatClient;

    private final RagServiceClient ragServiceClient;

    @Value("${llm.plan-generator.system-prompt}")
    private String basicSystemPrompt;

    @Value("${llm.plan-generator.rag-prompt}")
    private String ragSystemPrompt;


    public TrainingPlanLlmResponse generateTrainingPlan(String promptText, List<String> diseases) {
        boolean hasDiseases = !CollectionUtils.isEmpty(diseases);

        String ragContext = hasDiseases ? retrieveRagContext(diseases) : "";
        boolean useRag = hasDiseases && !ragContext.isEmpty();

        String finalSystemPrompt = useRag
                ? basicSystemPrompt + "\n\n" + ragSystemPrompt
                : basicSystemPrompt;

        try {
            return chatClient.prompt()
                    .system(s -> {
                        s.text(finalSystemPrompt);
                        if (useRag) {
                            s.param("context", ragContext);
                        }
                    })
                    .user(promptText)
                    .call()
                    .entity(TrainingPlanLlmResponse.class);

        } catch (Exception e) {
            log.error("Error generating training plan via Gemini API", e);
            throw new RuntimeException("Failed to generate plan with Spring AI", e);
        }
    }

    private String retrieveRagContext(List<String> diseases) {
        log.info("Retrieving RAG context for conditions: {}", diseases);

        List<RagDocumentResponse> uniqueDocs = diseases.stream()
                .map(ragServiceClient::search)
                .flatMap(List::stream)
                .distinct()
                .toList();

        log.info("Found {} unique relevant documents across all conditions", uniqueDocs.size());

        if (uniqueDocs.isEmpty()) {
            return "";
        }

        return uniqueDocs.stream()
                .map(RagDocumentResponse::getContent)
                .collect(Collectors.joining("\n---\n"));
    }
}
