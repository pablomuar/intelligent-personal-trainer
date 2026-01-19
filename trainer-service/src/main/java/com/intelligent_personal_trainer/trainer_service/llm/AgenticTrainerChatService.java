package com.intelligent_personal_trainer.trainer_service.llm;

import com.intelligent_personal_trainer.trainer_service.dto.ChatHistoryResponse;
import com.intelligent_personal_trainer.trainer_service.dto.ChatRequest;
import com.intelligent_personal_trainer.trainer_service.entity.ChatHistory;
import com.intelligent_personal_trainer.trainer_service.repository.ChatHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class AgenticTrainerChatService {

    private final ChatClient chatClient;
    private final ChatHistoryRepository chatHistoryRepository;

    private final List<ToolCallback> toolCallbacks;

    @Value("${llm.agentic-chat.system-prompt}")
    private String systemPrompt;

    public AgenticTrainerChatService(ChatClient chatClient, List<ToolCallbackProvider> toolProviders, ChatHistoryRepository chatHistoryRepository) {
        this.chatClient = chatClient;
        this.chatHistoryRepository = chatHistoryRepository;

        toolCallbacks = toolProviders.stream()
                .map(ToolCallbackProvider::getToolCallbacks)
                .flatMap(Arrays::stream)
                .toList();
    }

    public String chat(ChatRequest chatRequest) {
        log.info("Starting agentic chat for user: {}", chatRequest.getUserId());

        String finalSystemPrompt = systemPrompt +
                "\nThe user ID is: " + chatRequest.getUserId() +
                "\nThe current date is: " + LocalDate.now();

        String response = chatClient.prompt()
                .system(finalSystemPrompt)
                .user(u -> u.text(chatRequest.getPrompt()))
                .toolCallbacks(toolCallbacks)
                .call()
                .content();

        ChatHistory history = ChatHistory.builder()
                .userId(chatRequest.getUserId())
                .prompt(chatRequest.getPrompt())
                .response(response)
                .createdAt(LocalDateTime.now())
                .build();

        chatHistoryRepository.save(history);

        return response;
    }

    public List<ChatHistoryResponse> getChatHistory(String userId, LocalDate from, LocalDate to) {
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(23, 59, 59);

        return chatHistoryRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, fromDateTime, toDateTime)
                .stream()
                .map(h -> ChatHistoryResponse.builder()
                        .id(h.getId())
                        .prompt(h.getPrompt())
                        .response(h.getResponse())
                        .createdAt(h.getCreatedAt())
                        .build())
                .toList();
    }
}
