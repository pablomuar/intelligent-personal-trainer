package com.intelligent_personal_trainer.trainer_service.llm;

import com.intelligent_personal_trainer.trainer_service.dto.*;
import com.intelligent_personal_trainer.trainer_service.entity.ChatMessage;
import com.intelligent_personal_trainer.trainer_service.entity.Conversation;
import com.intelligent_personal_trainer.trainer_service.repository.ChatMessageRepository;
import com.intelligent_personal_trainer.trainer_service.repository.ConversationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
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

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    private AgenticTrainerChatService agenticTrainerChatService;

    @BeforeEach
    void setUp() {
        when(toolCallbackProvider.getToolCallbacks()).thenReturn(new ToolCallback[]{});
        agenticTrainerChatService = new AgenticTrainerChatService(chatClient, List.of(toolCallbackProvider), conversationRepository, chatMessageRepository);
        ReflectionTestUtils.setField(agenticTrainerChatService, "systemPrompt", "You are a trainer.");
        ReflectionTestUtils.setField(agenticTrainerChatService, "memoryWindowSize", 10);
    }

    @Test
    void testChat_NewConversation() {
        // Mock DB
        when(conversationRepository.save(any(Conversation.class))).thenAnswer(i -> {
            Conversation c = i.getArgument(0);
            c.setId(1L);
            return c;
        });
        when(chatMessageRepository.findByConversationIdOrderByCreatedAtDesc(any(), any()))
                .thenReturn(Collections.emptyList());

        // Mock ChatClient
        when(chatClient.prompt()).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.system(anyString())).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.messages(anyList())).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.user(any(Consumer.class))).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.toolCallbacks(anyList())).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("AI Response");

        ChatRequest request = new ChatRequest();
        request.setUserId("user123");
        request.setPrompt("Hello");

        ChatResponse response = agenticTrainerChatService.chat(request);

        assertEquals("AI Response", response.getResponse());
        assertNotNull(response.getConversationId());

        verify(chatClientRequestSpec).system(contains("You are a trainer."));
        verify(chatMessageRepository, times(2)).save(any(ChatMessage.class)); // 1 user, 1 ai
    }

    @Test
    void testGetConversations() {
        Conversation c = Conversation.builder()
                .id(1L)
                .title("Test")
                .updatedAt(LocalDateTime.now())
                .build();

        when(conversationRepository.findByUserIdOrderByUpdatedAtDesc("u1"))
                .thenReturn(List.of(c));

        List<ConversationResponse> result = agenticTrainerChatService.getConversations("u1");

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getTitle());
    }
}
