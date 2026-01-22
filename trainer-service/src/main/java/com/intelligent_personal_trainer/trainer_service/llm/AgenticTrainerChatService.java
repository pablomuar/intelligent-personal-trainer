package com.intelligent_personal_trainer.trainer_service.llm;

import com.intelligent_personal_trainer.trainer_service.dto.ChatRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class AgenticTrainerChatService {

    private final ChatClient chatClient;

    private final List<ToolCallback> toolCallbacks;

    @Value("${llm.agentic-chat.system-prompt}")
    private String systemPrompt;

    public AgenticTrainerChatService(ChatClient chatClient, List<ToolCallbackProvider> toolProviders) {
        this.chatClient = chatClient;

        toolCallbacks = toolProviders.stream()
                .map(ToolCallbackProvider::getToolCallbacks)
                .flatMap(Arrays::stream)
                .toList();
    }

    @Transactional
    public String chat(ChatRequest chatRequest, List<Message> historicMessages) {
        String finalSystemPrompt = systemPrompt +
                "\nThe user ID is: " + chatRequest.getUserId() +
                "\nThe current date is: " + LocalDate.now() +
                "\nUser Location: " + (chatRequest.getUserLocation() != null ? chatRequest.getUserLocation() : "Unknown");

        return chatClient.prompt()
                .system(finalSystemPrompt)
                .messages(historicMessages)
                .user(u -> u.text(chatRequest.getPrompt()))
                .toolCallbacks(toolCallbacks)
                .call()
                .content();
    }
}
