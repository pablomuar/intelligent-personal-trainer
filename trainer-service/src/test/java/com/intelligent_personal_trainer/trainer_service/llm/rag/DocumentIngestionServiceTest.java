package com.intelligent_personal_trainer.trainer_service.llm.rag;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DocumentIngestionServiceTest {

    @Mock
    private VectorStore vectorStore;

    @InjectMocks
    private DocumentIngestionService documentIngestionService;

    @Test
    void testIngestFile() {
        ReflectionTestUtils.setField(documentIngestionService, "chunkSize", 100);
        ReflectionTestUtils.setField(documentIngestionService, "minChunkSizeChars", 10);
        ReflectionTestUtils.setField(documentIngestionService, "minChunkLenghtToEmbed", 1);
        ReflectionTestUtils.setField(documentIngestionService, "maxNumChunks", 100);
        ReflectionTestUtils.setField(documentIngestionService, "keepSeparator", true);

        String content = "This is a test document content for RAG ingestion.";
        Resource resource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return "test.txt";
            }
        };

        documentIngestionService.ingestFile(resource);

        verify(vectorStore, times(1)).add(anyList());
    }
}
