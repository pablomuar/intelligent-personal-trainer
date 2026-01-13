package com.intelligent_personal_trainer.trainer_service.llm;

import com.intelligent_personal_trainer.trainer_service.llm.dto.TrainingPlanLlmResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingPlanGeneratorServiceTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private VectorStore vectorStore;

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

        verify(vectorStore, never()).similaritySearch(any(SearchRequest.class));
    }

    @Test
    void testGenerateTrainingPlan_WithDiseases_NoDocsFound() {
        ReflectionTestUtils.setField(trainingPlanGeneratorService, "basicSystemPrompt", "Basic System Prompt");
        ReflectionTestUtils.setField(trainingPlanGeneratorService, "ragSystemPrompt", "RAG System Prompt");
        ReflectionTestUtils.setField(trainingPlanGeneratorService, "topK", 3);
        ReflectionTestUtils.setField(trainingPlanGeneratorService, "similarityThreshold", 0.5);

        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(Collections.emptyList());

        when(chatClient.prompt()).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.system(any(Consumer.class))).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.user(anyString())).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.entity(TrainingPlanLlmResponse.class)).thenReturn(new TrainingPlanLlmResponse("Rec", "Analysis", Collections.emptyList(), null));

        trainingPlanGeneratorService.generateTrainingPlan("My Prompt", List.of("flu"));

        verify(vectorStore).similaritySearch(any(SearchRequest.class));
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
        ReflectionTestUtils.setField(trainingPlanGeneratorService, "topK", 3);
        ReflectionTestUtils.setField(trainingPlanGeneratorService, "similarityThreshold", 0.5);

        Document doc = new Document("Doc Content", Map.of("source", "wiki"));
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(doc));

        when(chatClient.prompt()).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.system(any(Consumer.class))).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.user(anyString())).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.entity(TrainingPlanLlmResponse.class)).thenReturn(new TrainingPlanLlmResponse("Rec", "Analysis", Collections.emptyList(), null));

        trainingPlanGeneratorService.generateTrainingPlan("My Prompt", List.of("flu"));

        verify(vectorStore).similaritySearch(any(SearchRequest.class));
        verify(chatClientRequestSpec).user("My Prompt");

        ArgumentCaptor<Consumer<ChatClient.PromptSystemSpec>> systemCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(chatClientRequestSpec).system(systemCaptor.capture());
        systemCaptor.getValue().accept(systemSpec);

        verify(systemSpec).text("Basic System Prompt\n\nRAG System Prompt");
        verify(systemSpec).param(eq("context"), contains("Doc Content"));
    }
}
