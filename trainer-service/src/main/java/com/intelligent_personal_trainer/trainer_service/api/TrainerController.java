package com.intelligent_personal_trainer.trainer_service.api;

import com.intelligent_personal_trainer.trainer_service.TrainerService;
import com.intelligent_personal_trainer.trainer_service.dto.ChatMessageResponse;
import com.intelligent_personal_trainer.trainer_service.dto.ChatRequest;
import com.intelligent_personal_trainer.trainer_service.dto.ChatResponse;
import com.intelligent_personal_trainer.trainer_service.dto.ConversationResponse;
import com.intelligent_personal_trainer.trainer_service.dto.TrainingPlanResponse;
import com.intelligent_personal_trainer.trainer_service.dto.TrainingRequest;
import com.intelligent_personal_trainer.trainer_service.llm.AgenticTrainerChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/trainer")
@RequiredArgsConstructor
@Tag(name = "Trainer Service", description = "API for AI-based workout plan generation and Agentic Chat")
public class TrainerController {

    private final TrainerService trainerService;

    private final AgenticTrainerChatService agenticTrainerChatService;

    @Operation(
            summary = "Generate workout plan",
            description = "Orchestrates the retrieval of user profile and activity history to generate a personalized plan using an LLM."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Plan generated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrainingPlanResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error while generating the plan (e.g., failure in AI or service communication)",
                    content = @Content
            )
    })
    @PostMapping("/plan")
    public ResponseEntity<TrainingPlanResponse> generatePlan(@RequestBody TrainingRequest request) {
        TrainingPlanResponse trainingPlanResponse = trainerService.createPlan(request);

        return trainingPlanResponse != null ?
                ResponseEntity.ok(trainingPlanResponse) :
                ResponseEntity.status(500).build();
    }

    @Operation(
            summary = "Chat with Agentic AI Trainer",
            description = "Sends a message to the AI trainer. If 'conversationId' is provided, the message is appended to that thread. If not, a new conversation is started. The AI uses context from the last N messages."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Message processed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Conversation not found (if conversationId provided)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error during chat processing",
                    content = @Content
            )
    })
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chatWithTrainer(@RequestBody ChatRequest request) {
        ChatResponse response = agenticTrainerChatService.chat(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get User Conversations",
            description = "Retrieves a list of all chat conversations for a specific user, ordered by most recent activity."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of conversations retrieved successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ConversationResponse.class)))
            )
    })
    @GetMapping("/chat/conversations")
    public ResponseEntity<List<ConversationResponse>> getConversations(
            @Parameter(description = "The ID of the user to retrieve conversations for", required = true)
            @RequestParam String userId) {
        return ResponseEntity.ok(agenticTrainerChatService.getConversations(userId));
    }

    @Operation(
            summary = "Get Conversation Messages",
            description = "Retrieves the full history of messages for a specific conversation, ordered chronologically."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Messages retrieved successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ChatMessageResponse.class)))
            )
    })
    @GetMapping("/chat/conversations/{id}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getConversationMessages(
            @Parameter(description = "The ID of the conversation to retrieve messages from", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(agenticTrainerChatService.getConversationMessages(id));
    }

    @Operation(
            summary = "Delete Conversation",
            description = "Deletes a specific conversation and all its associated messages."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conversation deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Conversation not found"
            )
    })
    @DeleteMapping("/chat/conversations/{id}")
    public ResponseEntity<Void> deleteConversation(
            @Parameter(description = "The ID of the conversation to delete", required = true)
            @PathVariable Long id) {
        agenticTrainerChatService.deleteConversation(id);
        return ResponseEntity.ok().build();
    }
}
