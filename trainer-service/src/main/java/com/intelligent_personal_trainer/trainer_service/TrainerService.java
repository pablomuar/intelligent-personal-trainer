package com.intelligent_personal_trainer.trainer_service;

import com.intelligent_personal_trainer.common.data.FitnessData;
import com.intelligent_personal_trainer.trainer_service.client.DataPersistenceServiceClient;
import com.intelligent_personal_trainer.trainer_service.client.UserServiceClient;
import com.intelligent_personal_trainer.trainer_service.dto.TrainingPlanResponse;
import com.intelligent_personal_trainer.trainer_service.dto.TrainingRequest;
import com.intelligent_personal_trainer.trainer_service.llm.LlmService;
import com.intelligent_personal_trainer.user_common.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerService {

    private final UserServiceClient userServiceClient;
    private final DataPersistenceServiceClient persistenceClient;

    private final LlmService llmService;

    /**
     * Orchestrates the creation of a training plan by fetching data from
     * multiple sources in parallel and querying the LLM.
     */
    public TrainingPlanResponse createPlan(TrainingRequest request) {
        String userId = request.getUserId();
        log.info("Starting training plan generation for user: {}", userId);

        CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() -> {
            try {
                log.debug("Fetching user profile for: {}", userId);
                return userServiceClient.getUser(userId);

            } catch (Exception e) {
                log.error("Error fetching user profile for {}", userId, e);
                return null;
            }
        });

        CompletableFuture<List<FitnessData>> fitnessDataHistoryFuture = CompletableFuture.supplyAsync(() -> {
            try {
                LocalDate fromDate = LocalDate.now().minusDays(request.getDaysHistory());
                log.debug("Fetching fitness history for {} since {}", userId, fromDate);
                return persistenceClient.getFitnessData(userId, fromDate.toString(), LocalDate.now().toString());

            } catch (Exception e) {
                log.warn("Error fetching fitness history for {}. Proceeding without history.", userId, e);
                return null;
            }
        });

        CompletableFuture.allOf(userFuture, fitnessDataHistoryFuture).join();

        User user = userFuture.join();
        List<FitnessData> fitnessDataHistory = fitnessDataHistoryFuture.join();

       if (user != null && fitnessDataHistory != null) {
           String prompt = buildLlmPrompt(user, fitnessDataHistory, request.getPrompt());

           // 5. Query the configured AI provider (transparent to this service)
           String planMarkdown = llmService.generateContent(prompt);

           log.info("Training plan generated successfully for user: {}", userId);

           return TrainingPlanResponse.builder()
                   .userId(userId)
                   .originalPrompt(request.getPrompt())
                   .trainingPlanMarkdown(planMarkdown)
                   .build();

       } else {
           return null;
       }
    }

    /**
     * Builds the prompt (Prompt Engineering) by combining profile and data.
     */
    private String buildLlmPrompt(User user, List<FitnessData> history, String userRequest) {
        StringBuilder sb = new StringBuilder();

        // System role
        sb.append("You are an expert Personal Trainer and Physiotherapist.\n\n");

        // User context
        sb.append("### User Profile\n");
        sb.append("- Name: ").append(user.getName()).append("\n");
        sb.append("- Age: ").append(user.getAge()).append("\n");
        sb.append("- Lifestyle: ").append(user.getLifestyle()).append("\n");

        if (user.getDiseases() != null && !user.getDiseases().isEmpty()) {
            sb.append("- Medical Conditions/Injuries: ")
                    .append(String.join(", ", user.getDiseases()))
                    .append("\n");
            sb.append("WARNING: Pay special attention to these conditions to avoid injury.\n");
        }
        sb.append("\n");

        // Historical context (recent data)
        sb.append("### Recent Activity (Last days)\n");
        if (history == null || history.isEmpty()) {
            sb.append("No recorded activity in the requested period.\n");
        } else {
            sb.append("Found ").append(history.size()).append(" activity records:\n");
            // A brief summary of the data to avoid saturating the context
            // We could compute averages here if the list is very long
            history.stream().limit(10).forEach(data ->
                    sb.append(String.format("- Date: %s | Steps: %.0f | Avg HR: %.0f | Calories: %.0f\n",
                            data.getTimestamp(), data.getTotalSteps(), data.getAverageHeartRate(), data.getTotalCaloriesBurned()))
            );
        }
        sb.append("\n");

        // User specific request
        sb.append("### User Goal/Request\n");
        sb.append(userRequest).append("\n\n");

        // Formatting instructions
        sb.append("### Instructions\n");
        sb.append("1. Create a detailed workout plan based on the profile and request.\n");
        sb.append("2. Adapt intensity based on recent activity and lifestyle.\n");
        sb.append("3. STRICTLY respect medical conditions.\n");
        sb.append("4. Return the output formatted in Markdown.");

        return sb.toString();
    }
}
