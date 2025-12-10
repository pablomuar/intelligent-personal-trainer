package com.intelligent_personal_trainer.trainer_service.api;

import com.intelligent_personal_trainer.trainer_service.TrainerService;
import com.intelligent_personal_trainer.trainer_service.dto.TrainingPlanResponse;
import com.intelligent_personal_trainer.trainer_service.dto.TrainingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trainer")
@RequiredArgsConstructor
@Tag(name = "Trainer Service", description = "API for AI-based workout plan generation")
public class TrainerController {

    private final TrainerService trainerService;

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
}