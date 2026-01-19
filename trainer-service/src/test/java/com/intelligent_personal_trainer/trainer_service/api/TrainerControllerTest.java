package com.intelligent_personal_trainer.trainer_service.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligent_personal_trainer.trainer_service.TrainerService;
import com.intelligent_personal_trainer.trainer_service.dto.ChatHistoryResponse;
import com.intelligent_personal_trainer.trainer_service.dto.ChatRequest;
import com.intelligent_personal_trainer.trainer_service.dto.TrainingPlanResponse;
import com.intelligent_personal_trainer.trainer_service.dto.TrainingRequest;
import com.intelligent_personal_trainer.trainer_service.llm.AgenticTrainerChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerController.class)
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerService trainerService;

    @MockBean
    private AgenticTrainerChatService agenticTrainerChatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGeneratePlan_Success() throws Exception {
        TrainingRequest request = new TrainingRequest();
        request.setUserId("user1");
        request.setPrompt("Goal");

        TrainingPlanResponse response = TrainingPlanResponse.builder()
                .userId("user1")
                .originalPrompt("Goal")
                .build();

        when(trainerService.createPlan(any(TrainingRequest.class))).thenReturn(response);

        mockMvc.perform(post("/trainer/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void testGeneratePlan_Failure() throws Exception {
        TrainingRequest request = new TrainingRequest();
        when(trainerService.createPlan(any(TrainingRequest.class))).thenReturn(null);

        mockMvc.perform(post("/trainer/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testChatWithTrainer() throws Exception {
        ChatRequest request = new ChatRequest();
        request.setPrompt("Hello");

        when(agenticTrainerChatService.chat(any(ChatRequest.class))).thenReturn("Hello there!");

        mockMvc.perform(post("/trainer/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello there!"));
    }

    @Test
    void testGetChatHistory() throws Exception {
        ChatHistoryResponse response = ChatHistoryResponse.builder()
                .id(1L)
                .prompt("Hi")
                .response("Hello")
                .build();

        when(agenticTrainerChatService.getChatHistory(any(), any(), any()))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/trainer/chat/history")
                        .param("userId", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(response))));
    }
}
