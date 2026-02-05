package com.intelligent_personal_trainer.data_processor_service.api;

import com.intelligent_personal_trainer.data_processor_service.DataProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/data-processor")
@RequiredArgsConstructor
@Tag(name = "Data Processor Service", description = "API to configure the data ingestion process")
public class DataProcessorController {

    private final DataProducerService dataProducerService;

    @Operation(
            summary = "Trigger data ingestion",
            description = "Reads data from a specific source for a given user and date, and sends it to Kafka."
    )
    @ApiResponse(responseCode = "202", description = "Request accepted and processed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @PostMapping("/trigger")
    public ResponseEntity<String> triggerIngestion(@Valid @RequestBody DataProcessorRequest request) {
        LocalDate startDate = request.date();
        LocalDate endDate = request.dateTo() != null ? request.dateTo() : startDate;

        if (endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest().body("End date cannot be before start date");
        }

        log.debug("Triggering data ingestion: {}", request);

        startDate.datesUntil(endDate.plusDays(1)).forEach(currentDate ->
                dataProducerService.processAndSendData(
                        request.sourceId(),
                        request.userId(),
                        request.externalSourceUserId(),
                        currentDate
                )
        );

        return ResponseEntity.accepted()
                .body("Ingestion triggered for user " + request.userId());
    }
}