package com.intelligent_personal_trainer.data_processor_service.api;

import com.intelligent_personal_trainer.data_processor_service.DataProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        dataProducerService.processAndSendData(
                request.sourceId(),
                request.userId(),
                request.date()
        );

        return ResponseEntity.accepted()
                .body("Ingestion triggered for user " + request.userId());
    }
}