package com.intelligent_personal_trainer.trainer_service.llm.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentIngestionService {

    private final VectorStore vectorStore;

    @Async
    public void ingestFile(Resource fileResource) {
        log.info("Starting RAG ingestion for: {}", fileResource.getFilename());

        try {
            // 1. Reader: Usa Tika para extraer texto y metadatos de cualquier formato
            TikaDocumentReader reader = new TikaDocumentReader(fileResource);
            List<Document> documents = reader.get();

            // 2. Transformer: TokenTextSplitter
            // Divide en chunks de 500 tokens (ventana segura para Gemini)
            TokenTextSplitter splitter = new TokenTextSplitter(500, 100, 5, 10000, true);
            List<Document> splitDocuments = splitter.apply(documents);

            // 3. Writer: VectorStore
            // Genera embeddings y guarda en Postgres automáticamente
            vectorStore.add(splitDocuments);

            log.info("Ingestion completed. {} fragments saved.", splitDocuments.size());

        } catch (Exception e) {
            log.error("Error processing document {}", fileResource.getFilename(), e);
        }
    }
}