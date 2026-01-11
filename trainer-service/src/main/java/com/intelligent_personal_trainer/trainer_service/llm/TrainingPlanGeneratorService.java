package com.intelligent_personal_trainer.trainer_service.llm;

import com.intelligent_personal_trainer.trainer_service.llm.dto.TrainingPlanLlmResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
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
    private final VectorStore vectorStore;

    @Value("${llm.system-prompt}")
    private String basicSystemPrompt;

    @Value("${llm.rag-prompt}")
    private String ragSystemPrompt;

    @Value("${llm.rag.search.top-k:3}")
    private int topK;

    @Value("${llm.rag.search.similarity-threshold:0.5}")
    private double similarityThreshold;

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
        log.info("Retrieving RAG context for conditions: {} with TopK: {} and Threshold: {}", diseases, topK, similarityThreshold);

        List<Document> uniqueDocs = diseases.stream()
                .map(this::performSearch)
                .flatMap(List::stream)
                .distinct()
                .toList();

        log.info("Found {} unique relevant documents across all conditions", uniqueDocs.size());

        if (uniqueDocs.isEmpty()) {
            return "";
        }

        return uniqueDocs.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n---\n"));
    }

    private List<Document> performSearch(String query) {
        List<Document> similaritySearch = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .similarityThreshold(similarityThreshold)
                        .build()
        );

        log.debug("Found {} relevant documents for \"{}\" query", similaritySearch.size(), query);
        similaritySearch.forEach(doc -> log.debug("Distance: {}, Source: {}", doc.getScore(), doc.getMetadata().get("source")));

        return similaritySearch;
    }
}