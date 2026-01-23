package com.intelligent_personal_trainer.trainer_service;

import com.intelligent_personal_trainer.trainer_service.dto.ChatMessageResponse;
import com.intelligent_personal_trainer.trainer_service.dto.ChatRequest;
import com.intelligent_personal_trainer.trainer_service.dto.ChatResponse;
import com.intelligent_personal_trainer.trainer_service.dto.ConversationResponse;
import com.intelligent_personal_trainer.trainer_service.entity.ChatMessage;
import com.intelligent_personal_trainer.trainer_service.entity.Conversation;
import com.intelligent_personal_trainer.trainer_service.llm.AgenticTrainerChatService;
import com.intelligent_personal_trainer.trainer_service.llm.dto.LlmResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligent_personal_trainer.trainer_service.repository.ChatMessageRepository;
import com.intelligent_personal_trainer.trainer_service.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerChatService {

    private final AgenticTrainerChatService agenticTrainerChatService;

    private final ConversationRepository conversationRepository;

    private final ChatMessageRepository chatMessageRepository;

    private final ObjectMapper objectMapper;

    @Value("${llm.trainer-chat.conversation.memory-window-size:10}")
    private int conversationMemoryWindowSize;

    @Transactional
    public ChatResponse chat(ChatRequest chatRequest) {
        log.info("Starting trainer chat for user: {}", chatRequest.getUserId());

        Conversation conversation = getOrCreateConversation(chatRequest);

        List<Message> historicMessages = getHistoricMessages(conversation.getId());

        persistChatMessage(conversation, ChatMessage.Role.USER, chatRequest.getPrompt());

        LlmResponse response = agenticTrainerChatService.chat(chatRequest, historicMessages);
        ChatResponse chatResponse = ChatResponse.builder()
                .conversationId(conversation.getId())
                .title(conversation.getTitle())
                .response(response)
                .build();

        try {
            persistChatMessage(conversation, ChatMessage.Role.ASSISTANT, objectMapper.writeValueAsString(response));

        } catch (JsonProcessingException e) {
            log.error("Error serializing LlmResponse to JSON", e);
            throw new RuntimeException("Error saving chat response", e);
        }

        log.info("Finished trainer chat with {} history size for user: {}", historicMessages.size(), chatRequest.getUserId());

        return chatResponse;
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

    @Transactional
    public void deleteConversation(Long conversationId) {
        conversationRepository.deleteById(conversationId);
    }

    private Conversation getOrCreateConversation(ChatRequest chatRequest) {
        Conversation conversation;
        if (chatRequest.getConversationId() != null) {
            conversation = conversationRepository.findById(chatRequest.getConversationId())
                    .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        } else {
            String prompt = chatRequest.getPrompt();
            String title = prompt.length() > 50 ? prompt.substring(0, 50) + "..." : prompt;

            conversation = Conversation.builder()
                    .userId(chatRequest.getUserId())
                    .title(title)
                    .build();
        }

        conversation.setUpdatedAt(LocalDateTime.now());
        return conversationRepository.save(conversation);
    }

    private List<Message> getHistoricMessages(Long conversationId) {
        List<ChatMessage> historyEntities = chatMessageRepository.findByConversationIdOrderByCreatedAtDesc(
                conversationId,
                PageRequest.of(0, conversationMemoryWindowSize)
        );

        return historyEntities.stream()
                .sorted(Comparator.comparing(ChatMessage::getCreatedAt))
                .map(chatMessage -> chatMessage.getRole() == ChatMessage.Role.USER ?
                        new UserMessage(chatMessage.getContent()) :
                        new AssistantMessage(chatMessage.getContent()))
                .map(Message.class::cast)
                .toList();
    }

    private void persistChatMessage(Conversation conversation, ChatMessage.Role role, String content) {
        ChatMessage aiMsg = ChatMessage.builder()
                .conversationId(conversation.getId())
                .role(role)
                .content(content)
                .build();

        chatMessageRepository.save(aiMsg);
    }
}
