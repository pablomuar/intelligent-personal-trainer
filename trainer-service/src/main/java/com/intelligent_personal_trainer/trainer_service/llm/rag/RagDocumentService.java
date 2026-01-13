package com.intelligent_personal_trainer.trainer_service.llm.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagDocumentService {

    private final VectorStore vectorStore;

    @Value("${llm.rag.search.top-k:3}")
    private int topK;

    @Value("${llm.rag.search.similarity-threshold:0.5}")
    private double similarityThreshold;

    public List<Document> performSearch(String query) {
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
