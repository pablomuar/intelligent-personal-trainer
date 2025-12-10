package com.intelligent_personal_trainer.data_persistence_service;

import com.intelligent_personal_trainer.common.data.FitnessData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.PastOrPresent;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/data-persistence")
@RequiredArgsConstructor
@Tag(name = "Data Persistence API", description = "API to retrieve fitness data history")
public class DataPersistenceController {

    private final FitnessDataPersistenceService persistenceService;

    @Operation(
            summary = "Retrieve fitness data for a user",
            description = "Returns the historical fitness data. Allows filtering by date range. Provided dates are interpreted in UTC timezone."
    )
    @ApiResponse(responseCode = "200", description = "Data persistence retrieval successful")
    @GetMapping("/fitness-data/{userId}")
    public ResponseEntity<List<FitnessData>> getFitnessData(
            @Parameter(description = "User ID", required = true, example = "user123")
            @PathVariable("userId") String userId,

            @Parameter(description = "Start date (YYYY-MM-DD). Interpreted as the start of the day in UTC (00:00Z).", example = "2023-10-27")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @PastOrPresent(message = "The 'from' date cannot be in the future")
            LocalDate from,

            @Parameter(description = "End date (YYYY-MM-DD). Interpreted as the end of that day in UTC. If omitted, searches up to the current moment.", example = "2023-10-28")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @PastOrPresent(message = "The 'to' date cannot be in the future")
            LocalDate to
    ) {
        List<FitnessData> data = persistenceService.getFitnessDataByUser(userId, from, to);
        return ResponseEntity.ok(data);
    }
}