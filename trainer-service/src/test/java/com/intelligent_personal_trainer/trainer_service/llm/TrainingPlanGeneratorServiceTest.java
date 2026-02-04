package com.intelligent_personal_trainer.trainer_service.llm;

import com.intelligent_personal_trainer.trainer_service.client.RagServiceClient;
import com.intelligent_personal_trainer.trainer_service.llm.dto.TrainingPlanLlmResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingPlanGeneratorServiceTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private RagServiceClient ragServiceClient;

    @Mock
    private ChatClient.ChatClientRequestSpec chatClientRequestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    @Mock
    private ChatClient.PromptSystemSpec systemSpec;

    @InjectMocks
    private TrainingPlanGeneratorService trainingPlanGeneratorService;

    @Test
    void testGenerateTrainingPlan_NoDiseases() {
        ReflectionTestUtils.setField(trainingPlanGeneratorService, "basicSystemPrompt", "Basic System Prompt");
        ReflectionTestUtils.setField(trainingPlanGeneratorService, "ragSystemPrompt", "RAG System Prompt");

        when(chatClient.prompt()).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.system(any(Consumer.class))).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.user(anyString())).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.entity(TrainingPlanLlmResponse.class)).thenReturn(new TrainingPlanLlmResponse("Rec", "Analysis", Collections.emptyList(), null));

        trainingPlanGeneratorService.generateTrainingPlan("My Prompt", Collections.emptyList());

        verify(chatClient).prompt();
        verify(chatClientRequestSpec).user("My Prompt");

        ArgumentCaptor<Consumer<ChatClient.PromptSystemSpec>> systemCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(chatClientRequestSpec).system(systemCaptor.capture());
        systemCaptor.getValue().accept(systemSpec);
        verify(systemSpec).text("Basic System Prompt");

        verify(ragServiceClient, never()).search(anyString());
    }

    @Test
    void testGenerateTrainingPlan_WithDiseases_NoDocsFound() {
        ReflectionTestUtils.setField(trainingPlanGeneratorService, "basicSystemPrompt", "Basic System Prompt");
        ReflectionTestUtils.setField(trainingPlanGeneratorService, "ragSystemPrompt", "RAG System Prompt");

        when(ragServiceClient.search(anyString())).thenReturn(Collections.emptyList());

        when(chatClient.prompt()).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.system(any(Consumer.class))).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.user(anyString())).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.entity(TrainingPlanLlmResponse.class)).thenReturn(new TrainingPlanLlmResponse("Rec", "Analysis", Collections.emptyList(), null));

        trainingPlanGeneratorService.generateTrainingPlan("My Prompt", List.of("flu"));

        verify(ragServiceClient).search(anyString());
        verify(chatClientRequestSpec).user("My Prompt");

        ArgumentCaptor<Consumer<ChatClient.PromptSystemSpec>> systemCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(chatClientRequestSpec).system(systemCaptor.capture());
        systemCaptor.getValue().accept(systemSpec);
        verify(systemSpec).text("Basic System Prompt");
    }

    @Test
    void testGenerateTrainingPlan_WithDiseases_DocsFound() {
        ReflectionTestUtils.setField(trainingPlanGeneratorService, "basicSystemPrompt", "Basic System Prompt");
        ReflectionTestUtils.setField(trainingPlanGeneratorService, "ragSystemPrompt", "RAG System Prompt");

        Document doc = new Document("Doc Content", Map.of("source", "wiki"));
        when(ragServiceClient.search(anyString())).thenReturn(List.of(doc));

        when(chatClient.prompt()).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.system(any(Consumer.class))).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.user(anyString())).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.entity(TrainingPlanLlmResponse.class)).thenReturn(new TrainingPlanLlmResponse("Rec", "Analysis", Collections.emptyList(), null));

        trainingPlanGeneratorService.generateTrainingPlan("My Prompt", List.of("flu"));

        verify(ragServiceClient).search(anyString());
        verify(chatClientRequestSpec).user("My Prompt");

        ArgumentCaptor<Consumer<ChatClient.PromptSystemSpec>> systemCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(chatClientRequestSpec).system(systemCaptor.capture());
        systemCaptor.getValue().accept(systemSpec);

        verify(systemSpec).text("Basic System Prompt\n\nRAG System Prompt");
        verify(systemSpec).param(eq("context"), contains("Doc Content"));
    }
}
