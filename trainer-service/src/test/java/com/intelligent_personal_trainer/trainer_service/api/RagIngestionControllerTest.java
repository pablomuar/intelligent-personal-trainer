package com.intelligent_personal_trainer.trainer_service.api;

import com.intelligent_personal_trainer.trainer_service.llm.rag.DocumentIngestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RagIngestionController.class)
class RagIngestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentIngestionService documentIngestionService;

    @Test
    void testIngestDocument_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());

        mockMvc.perform(multipart("/trainer/rag/ingest").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Ingestion process started in background for: test.txt"));

        verify(documentIngestionService).ingestFile(any());
    }

    @Test
    void testIngestDocument_EmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "empty.txt", "text/plain", "".getBytes());

        mockMvc.perform(multipart("/trainer/rag/ingest").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The file is empty"));
    }
}
