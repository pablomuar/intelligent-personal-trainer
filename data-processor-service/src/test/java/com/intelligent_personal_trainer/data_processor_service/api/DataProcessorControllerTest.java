package com.intelligent_personal_trainer.data_processor_service.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligent_personal_trainer.data_processor_service.DataProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DataProcessorController.class)
class DataProcessorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataProducerService dataProducerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void triggerIngestion_shouldReturnAcceptedAndCallService() throws Exception {
        DataProcessorRequest request = new DataProcessorRequest("user123", "extUser123", "source456", LocalDate.now().minusDays(1));

        mockMvc.perform(post("/data-processor/trigger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Ingestion triggered for user user123"));

        verify(dataProducerService).processAndSendData("source456", "user123", "extUser123", request.date());
    }

    @Test
    void triggerIngestion_shouldReturnBadRequest_whenDateIsInFuture() throws Exception {
        DataProcessorRequest request = new DataProcessorRequest("user123", "extUser123", "source456", LocalDate.now().plusDays(1));

        mockMvc.perform(post("/data-processor/trigger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void triggerIngestion_shouldReturnBadRequest_whenUserIdIsEmpty() throws Exception {
        DataProcessorRequest request = new DataProcessorRequest("", "extUser123", "source456", LocalDate.now().minusDays(1));

        mockMvc.perform(post("/data-processor/trigger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void triggerIngestion_shouldReturnBadRequest_whenExternalSourceUserIdIsEmpty() throws Exception {
        DataProcessorRequest request = new DataProcessorRequest("user123", "", "source456", LocalDate.now().minusDays(1));

        mockMvc.perform(post("/data-processor/trigger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void triggerIngestion_shouldReturnBadRequest_whenSourceIdIsEmpty() throws Exception {
        DataProcessorRequest request = new DataProcessorRequest("user123", "extUser123", "", LocalDate.now().minusDays(1));

        mockMvc.perform(post("/data-processor/trigger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
