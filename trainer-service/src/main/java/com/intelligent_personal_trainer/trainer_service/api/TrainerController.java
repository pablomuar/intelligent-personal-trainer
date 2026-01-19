package com.intelligent_personal_trainer.trainer_service.api;

import com.intelligent_personal_trainer.trainer_service.TrainerService;
import com.intelligent_personal_trainer.trainer_service.dto.ChatHistoryResponse;
import com.intelligent_personal_trainer.trainer_service.dto.ChatRequest;
import com.intelligent_personal_trainer.trainer_service.dto.TrainingPlanResponse;
import com.intelligent_personal_trainer.trainer_service.dto.TrainingRequest;
import com.intelligent_personal_trainer.trainer_service.llm.AgenticTrainerChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/trainer")
@RequiredArgsConstructor
@Tag(name = "Trainer Service", description = "API for AI-based workout plan generation")
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
            description = "Allows the user to send messages to the Agentic AI trainer and receive contextualized responses based on their profile and history."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Message processed successfully",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error during chat processing",
                    content = @Content
            )
    })
    @PostMapping("/chat")
    public ResponseEntity<String> chatWithTrainer(@RequestBody ChatRequest request) {
        String response = agenticTrainerChatService.chat(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get Chat History")
    @GetMapping("/chat/history")
    public ResponseEntity<List<ChatHistoryResponse>> getChatHistory(
            @RequestParam String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        if (from == null) from = LocalDate.now().minusDays(30);
        if (to == null) to = LocalDate.now();

        return ResponseEntity.ok(agenticTrainerChatService.getChatHistory(userId, from, to));
    }
}
