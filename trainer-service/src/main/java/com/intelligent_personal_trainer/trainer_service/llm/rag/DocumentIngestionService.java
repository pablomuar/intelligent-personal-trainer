package com.intelligent_personal_trainer.trainer_service.llm.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentIngestionService {

    private final VectorStore vectorStore;

    @Value("${llm.rag.ingestion.chunkSize:1000}")
    private int chunkSize;

    @Value("${llm.rag.ingestion.minChunkSizeChars:300}")
    private int minChunkSizeChars;

    @Value("${llm.rag.ingestion.minChunkLenghtToEmbed:5}")
    private int minChunkLenghtToEmbed;

    @Value("${llm.rag.ingestion.maxNumChunks:10000}")
    private int maxNumChunks;

    @Value("${llm.rag.ingestion.keepSeparator:true}")
    private boolean keepSeparator;

    @Async
    public void ingestFile(Resource fileResource) {
        log.info("Starting RAG ingestion for: {}", fileResource.getFilename());

        try {
            TikaDocumentReader reader = new TikaDocumentReader(fileResource);
            List<Document> documents = reader.get();

            TokenTextSplitter splitter = new TokenTextSplitter(chunkSize, minChunkSizeChars, minChunkLenghtToEmbed, maxNumChunks, keepSeparator);
            List<Document> splitDocuments = splitter.apply(documents);

            vectorStore.add(splitDocuments);

            log.info("Ingestion completed. {} fragments saved.", splitDocuments.size());

        } catch (Exception e) {
            log.error("Error processing document {}", fileResource.getFilename(), e);
        }
    }
}