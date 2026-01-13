package com.intelligent_personal_trainer.trainer_service.llm;

import com.intelligent_personal_trainer.trainer_service.dto.ChatRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgenticTrainerChatServiceTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec chatClientRequestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    @Mock
    private ToolCallbackProvider toolCallbackProvider;

    private AgenticTrainerChatService agenticTrainerChatService;

    @BeforeEach
    void setUp() {
        when(toolCallbackProvider.getToolCallbacks()).thenReturn(new ToolCallback[]{});
        agenticTrainerChatService = new AgenticTrainerChatService(chatClient, List.of(toolCallbackProvider));
    }

    @Test
    void testChat() {
        when(chatClient.prompt()).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.system(anyString())).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.user(any(Consumer.class))).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.toolCallbacks(anyList())).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("AI Response");

        ChatRequest request = new ChatRequest();
        request.setUserId("user123");
        request.setPrompt("Hello");

        String response = agenticTrainerChatService.chat(request);

        assertEquals("AI Response", response);
        verify(chatClientRequestSpec).system(anyString());
        verify(chatClientRequestSpec).user(any(Consumer.class));
        verify(chatClientRequestSpec).toolCallbacks(anyList());
    }
}
