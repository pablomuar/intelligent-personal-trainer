package com.intelligent_personal_trainer.trainer_service.llm.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagDocumentTool implements ToolCallbackProvider {

    private final RagDocumentService ragDocumentService;

    @Tool(description = "Searches the medical knowledge base for clinical guidelines, contraindications, and recommendations. Use this whenever a pathology is mentioned.")
    public String searchMedicalKnowledge(String query) {
        log.info("Retrieving RAG context for query: {}", query);

        List<Document> results = ragDocumentService.performSearch(query);

        if (results.isEmpty()) {
            log.info("No documents found for query: {}", query);
            return "No specific information found in the medical knowledge base.";
        }

        log.info("Found {} documents for query: {}", results.size(), query);

        return results.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n---\n"));
    }

    @NotNull
    @Override
    public ToolCallback[] getToolCallbacks() {
        return ToolCallbacks.from(this);
    }
}