package com.intelligent_personal_trainer.trainer_service.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligent_personal_trainer.trainer_service.TrainerService;
import com.intelligent_personal_trainer.trainer_service.dto.*;
import com.intelligent_personal_trainer.trainer_service.llm.dto.LlmResponse;
import com.intelligent_personal_trainer.trainer_service.TrainerChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
    private TrainerChatService trainerChatService;

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

        LlmResponse llmResponse = new LlmResponse("Hello there!", null, LlmResponse.ResponseType.CHAT_ONLY);
        ChatResponse response = ChatResponse.builder()
                .conversationId(1L)
                .title("New Chat")
                .response(llmResponse)
                .build();

        when(trainerChatService.chat(any(ChatRequest.class))).thenReturn(response);

        mockMvc.perform(post("/trainer/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void testGetConversations() throws Exception {
        ConversationResponse response = ConversationResponse.builder()
                .id(1L)
                .title("Test Chat")
                .lastMessageAt(Instant.now())
                .build();

        when(trainerChatService.getConversations(anyString()))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/trainer/chat/conversations")
                        .param("userId", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(response))));
    }

    @Test
    void testGetConversationMessages() throws Exception {
        ChatMessageResponse response = ChatMessageResponse.builder()
                .id(1L)
                .content("Hello")
                .build();

        when(trainerChatService.getConversationMessages(anyLong()))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/trainer/chat/conversations/1/messages"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(response))));
    }
}
