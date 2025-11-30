package com.intelligent_personal_trainer.data_processor_service.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record DataProcessorRequest(
        @Schema(description = "ID of the user to process", example = "100")
        @NotBlank(message = "User ID cannot be empty")
        String userId,

        @Schema(description = "ID of the data source configured in sources.json", example = "EXAMPLE_DATASET")
        @NotBlank(message = "Source ID cannot be empty")
        String sourceId,

        @Schema(description = "Date of the data to extract (YYYY-MM-DD)", example = "2023-12-19")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "Date is required")
        @PastOrPresent(message = "Date cannot be in the future")
        LocalDate date
) {

}