package com.intelligent_personal_trainer.trainer_service.llm;

import com.intelligent_personal_trainer.trainer_service.dto.ChatMessageResponse;
import com.intelligent_personal_trainer.trainer_service.dto.ChatRequest;
import com.intelligent_personal_trainer.trainer_service.dto.ChatResponse;
import com.intelligent_personal_trainer.trainer_service.dto.ConversationResponse;
import com.intelligent_personal_trainer.trainer_service.entity.ChatMessage;
import com.intelligent_personal_trainer.trainer_service.entity.Conversation;
import com.intelligent_personal_trainer.trainer_service.repository.ChatMessageRepository;
import com.intelligent_personal_trainer.trainer_service.repository.ConversationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class AgenticTrainerChatService {

    private final ChatClient chatClient;
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final List<ToolCallback> toolCallbacks;

    @Value("${llm.agentic-chat.system-prompt}")
    private String systemPrompt;

    @Value("${llm.agentic-chat.memory-window-size:10}")
    private int memoryWindowSize;

    public AgenticTrainerChatService(ChatClient chatClient,
                                     List<ToolCallbackProvider> toolProviders,
                                     ConversationRepository conversationRepository,
                                     ChatMessageRepository chatMessageRepository) {
        this.chatClient = chatClient;
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;

        toolCallbacks = toolProviders.stream()
                .map(ToolCallbackProvider::getToolCallbacks)
                .flatMap(Arrays::stream)
                .toList();
    }

    @Transactional
    public ChatResponse chat(ChatRequest chatRequest) {
        log.info("Starting agentic chat for user: {}", chatRequest.getUserId());

        // 1. Get or Create Conversation
        Conversation conversation;
        if (chatRequest.getConversationId() != null) {
            conversation = conversationRepository.findById(chatRequest.getConversationId())
                    .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));
            // Explicitly saving to update @UpdateTimestamp
            conversation = conversationRepository.save(conversation);
        } else {
            String title = chatRequest.getPrompt().length() > 50 ?
                    chatRequest.getPrompt().substring(0, 50) + "..." :
                    chatRequest.getPrompt();

            conversation = Conversation.builder()
                    .userId(chatRequest.getUserId())
                    .title(title)
                    .build();
            conversation = conversationRepository.save(conversation);
        }

        // 2. Retrieve History (Context)
        List<ChatMessage> historyEntities = chatMessageRepository.findByConversationIdOrderByCreatedAtDesc(
                conversation.getId(),
                PageRequest.of(0, memoryWindowSize)
        );

        // Reverse to chronological order (Oldest -> Newest)
        List<Message> historyMessages = new ArrayList<>();
        for (int i = historyEntities.size() - 1; i >= 0; i--) {
            ChatMessage cm = historyEntities.get(i);
            if (cm.getRole() == ChatMessage.Role.USER) {
                historyMessages.add(new UserMessage(cm.getContent()));
            } else {
                historyMessages.add(new AssistantMessage(cm.getContent()));
            }
        }

        // 3. Prepare System Prompt
        String finalSystemPrompt = systemPrompt +
                "\nThe user ID is: " + chatRequest.getUserId() +
                "\nThe current date is: " + LocalDate.now();

        // 4. Call LLM
        String responseContent = chatClient.prompt()
                .system(finalSystemPrompt)
                .messages(historyMessages)
                .user(u -> u.text(chatRequest.getPrompt()))
                .toolCallbacks(toolCallbacks)
                .call()
                .content();

        // 5. Save Messages
        ChatMessage userMsg = ChatMessage.builder()
                .conversationId(conversation.getId())
                .role(ChatMessage.Role.USER)
                .content(chatRequest.getPrompt())
                .build();
        chatMessageRepository.save(userMsg);

        ChatMessage aiMsg = ChatMessage.builder()
                .conversationId(conversation.getId())
                .role(ChatMessage.Role.ASSISTANT)
                .content(responseContent)
                .build();
        chatMessageRepository.save(aiMsg);

        return ChatResponse.builder()
                .conversationId(conversation.getId())
                .title(conversation.getTitle())
                .response(responseContent)
                .build();
    }

    public List<ConversationResponse> getConversations(String userId) {
        return conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(c -> ConversationResponse.builder()
                        .id(c.getId())
                        .title(c.getTitle())
                        .lastMessageAt(c.getUpdatedAt() != null ? c.getUpdatedAt() : c.getCreatedAt())
                        .build())
                .toList();
    }

    public List<ChatMessageResponse> getConversationMessages(Long conversationId) {
        return chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                .map(m -> ChatMessageResponse.builder()
                        .id(m.getId())
                        .role(m.getRole())
                        .content(m.getContent())
                        .createdAt(m.getCreatedAt())
                        .build())
                .toList();
    }
}
