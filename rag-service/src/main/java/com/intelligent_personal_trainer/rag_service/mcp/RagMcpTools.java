package com.intelligent_personal_trainer.rag_service.mcp;

import com.intelligent_personal_trainer.rag_service.service.RagDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RagMcpTools {

    private final RagDocumentService ragDocumentService;

    @McpTool(name = "searchMedicalKnowledge", description = "Searches the medical knowledge base for clinical guidelines, contraindications, and recommendations. Use this whenever a pathology is mentioned.")
    public String searchMedicalKnowledge(
            @McpToolParam(description = "The query or condition to search for") String query
    ) {
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
}
