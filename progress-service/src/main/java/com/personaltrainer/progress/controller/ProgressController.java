package com.personaltrainer.progress.controller;

import com.personaltrainer.progress.entity.ProgressRecord;
import com.personaltrainer.progress.repository.ProgressRecordRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/progress")
@CrossOrigin(origins = "*")
public class ProgressController {

    @Autowired
    private ProgressRecordRepository progressRecordRepository;

    @GetMapping
    public ResponseEntity<List<ProgressRecord>> getAllProgressRecords() {
        List<ProgressRecord> records = progressRecordRepository.findAll();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgressRecord> getProgressRecordById(@PathVariable Long id) {
        return progressRecordRepository.findById(id)
                .map(record -> ResponseEntity.ok(record))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProgressRecord>> getProgressRecordsByUserId(@PathVariable Long userId) {
        List<ProgressRecord> records = progressRecordRepository.findByUserId(userId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/user/{userId}/workout/{workoutPlanId}")
    public ResponseEntity<List<ProgressRecord>> getProgressRecordsByUserAndWorkout(
            @PathVariable Long userId, @PathVariable Long workoutPlanId) {
        List<ProgressRecord> records = progressRecordRepository.findByUserIdAndWorkoutPlanId(userId, workoutPlanId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/user/{userId}/analytics")
    public ResponseEntity<Map<String, Object>> getUserAnalytics(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        Integer totalCalories = progressRecordRepository.getTotalCaloriesBurnedByUserAndDateRange(userId, startDate, endDate);
        Long workoutCount = progressRecordRepository.getWorkoutCountByUserAndDateRange(userId, startDate, endDate);
        List<ProgressRecord> recentRecords = progressRecordRepository.findByUserIdAndCompletedAtBetween(userId, startDate, endDate);
        
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalCaloriesBurned", totalCalories != null ? totalCalories : 0);
        analytics.put("totalWorkouts", workoutCount);
        analytics.put("recentRecords", recentRecords);
        analytics.put("dateRange", Map.of("start", startDate, "end", endDate));
        
        return ResponseEntity.ok(analytics);
    }

    @PostMapping
    public ResponseEntity<ProgressRecord> createProgressRecord(@Valid @RequestBody ProgressRecord progressRecord) {
        ProgressRecord savedRecord = progressRecordRepository.save(progressRecord);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRecord);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgressRecord> updateProgressRecord(@PathVariable Long id, @Valid @RequestBody ProgressRecord recordDetails) {
        return progressRecordRepository.findById(id)
                .map(record -> {
                    record.setSetsCompleted(recordDetails.getSetsCompleted());
                    record.setRepsCompleted(recordDetails.getRepsCompleted());
                    record.setWeightUsed(recordDetails.getWeightUsed());
                    record.setDurationMinutes(recordDetails.getDurationMinutes());
                    record.setCaloriesBurned(recordDetails.getCaloriesBurned());
                    record.setNotes(recordDetails.getNotes());
                    record.setCompletedAt(recordDetails.getCompletedAt());
                    return ResponseEntity.ok(progressRecordRepository.save(record));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProgressRecord(@PathVariable Long id) {
        return progressRecordRepository.findById(id)
                .map(record -> {
                    progressRecordRepository.delete(record);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Progress Service is running!");
    }
}