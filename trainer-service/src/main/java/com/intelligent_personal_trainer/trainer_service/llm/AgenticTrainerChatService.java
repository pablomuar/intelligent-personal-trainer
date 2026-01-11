package com.intelligent_personal_trainer.trainer_service.llm;

import com.intelligent_personal_trainer.trainer_service.dto.ChatRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AgenticTrainerChatService {

    private final ChatClient chatClient;

    private final List<ToolCallback> toolCallbacks;

    public AgenticTrainerChatService(ChatClient chatClient, List<ToolCallbackProvider> toolProviders) {
        this.chatClient = chatClient;

        toolCallbacks = toolProviders.stream()
                .map(ToolCallbackProvider::getToolCallbacks)
                .flatMap(Arrays::stream)
                .toList();
    }

    public String chat(ChatRequest chatRequest) {
        return chatClient.prompt()
                .system("""
                    Eres un entrenador personal experto e inteligente. Tu objetivo es crear planes o responder dudas basándote en DATOS REALES del usuario.
                    
                    HERRAMIENTAS DISPONIBLES:
                    Tienes acceso a herramientas remotas para consultar:
                    1. Perfil del usuario (getUserProfile): edad, peso, información medica, lesiones, enfermedades, etc.
                    2. Historial de entrenamientos (getFitnessData): sesiones pasadas.
                    
                    REGLAS DE OPERACIÓN:
                    - ANTES de responder, VERIFICA si tienes el contexto necesario. Si no, USA las herramientas.
                    - Si el usuario pregunta "cómo voy este mes", consulta el historial con un rango de fechas adecuado.
                    - Si el usuario menciona dolor o lesiones o información médica, consulta su perfil.
                    - Sé amable, motivador y conciso.
                    """)
                .user(u -> u.text(chatRequest.getPrompt() + ". Mi ID de usuario es: " + chatRequest.getUserId()))
                .toolCallbacks(toolCallbacks)
                .call()
                .content();
    }
}
