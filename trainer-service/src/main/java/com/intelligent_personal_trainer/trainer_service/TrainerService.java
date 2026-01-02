package com.intelligent_personal_trainer.trainer_service;

import com.intelligent_personal_trainer.common.data.FitnessData;
import com.intelligent_personal_trainer.trainer_service.client.DataPersistenceServiceClient;
import com.intelligent_personal_trainer.trainer_service.client.UserServiceClient;
import com.intelligent_personal_trainer.trainer_service.dto.TrainingPlanResponse;
import com.intelligent_personal_trainer.trainer_service.dto.TrainingRequest;
import com.intelligent_personal_trainer.trainer_service.llm.TrainingPlanGeneratorService;
import com.intelligent_personal_trainer.trainer_service.llm.dto.TrainingPlanLlmResponse;
import com.intelligent_personal_trainer.user_common.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainingPlanGeneratorService trainingPlanGeneratorService;
    private final DataPersistenceServiceClient persistenceClient;
    private final UserServiceClient userServiceClient;

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

           TrainingPlanLlmResponse trainingPlanResponse = trainingPlanGeneratorService.generateTrainingPlan(prompt, user.getDiseases());

           log.info("Training plan generated successfully for user: {}", userId);

           return TrainingPlanResponse.builder()
                   .userId(userId)
                   .originalPrompt(request.getPrompt())
                   .trainingPlan(trainingPlanResponse)
                   .build();

       } else {
           log.error("Failed to generate training plan for user: {} due to missing data. Found user: {}. Found fitness data {}",
                   userId, user != null, fitnessDataHistory != null);
           return null;
       }
    }

    private String buildLlmPrompt(User user, List<FitnessData> history, String userRequest) {
        StringBuilder sb = new StringBuilder();

        // User context
        sb.append("### User Profile\n");
        sb.append("- Name: ").append(user.getName()).append("\n");
        sb.append("- Age: ").append(user.getAge()).append("\n");
        sb.append("- Lifestyle: ").append(user.getLifestyle()).append("\n");

        // Diseases
        if (user.getDiseases() != null && !user.getDiseases().isEmpty()) {
            sb.append("- Medical Conditions: ")
                    .append(String.join(", ", user.getDiseases()))
                    .append("\n");
            sb.append("WARNING: Pay special attention to these conditions to avoid injury.\n");
        }
        sb.append("\n");


        sb.append("### Recent Activity (Last days)\n");
        if (history != null && !history.isEmpty()) {
            sb.append("Recent activity:\n");

            history.stream().limit(10).forEach(data -> {
                        LocalDate recordDate = data.getTimestamp().atZone(ZoneId.systemDefault()).toLocalDate();
                        long daysDiff = LocalDate.now().toEpochDay() - recordDate.toEpochDay();
                        String dateLabel;
                        if (daysDiff == 0) {
                            dateLabel = "Today";
                        } else if (daysDiff == 1) {
                            dateLabel = "Yesterday";
                        } else {
                            dateLabel = daysDiff + " days ago";
                        }

                        sb.append(String.format("- Date: %s | Steps: %.0f | Avg HR: %.0f | Calories: %.0f",
                                dateLabel, data.getTotalSteps(), data.getAverageHeartRate(), data.getTotalCaloriesBurned()));
                        if(data.getWorkoutDataList() == null || data.getWorkoutDataList().isEmpty()) {
                            sb.append("No recorded specific workout records.\n");

                        } else {
                            sb.append(" | Workouts: ")
                                    .append(data.getWorkoutDataList());
                        }
                        sb.append("\n");
                    }
            );

            sb.append("\n");
        }

        // User specific request
        sb.append("### User Goal/Request\n");
        sb.append(userRequest);

        return sb.toString();
    }
}
