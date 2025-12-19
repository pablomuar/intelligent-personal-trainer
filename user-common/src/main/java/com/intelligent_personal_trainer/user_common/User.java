package com.intelligent_personal_trainer.user_common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class User {

    @Schema(description = "Unique system ID managed by User Service")
    private String userId;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Surname is required")
    private String surname;

    @Min(value = 0, message = "Age must be positive")
    private Integer age;

    @Schema(description = "Height in centimeters")
    @Min(value = 0, message = "Height must be positive")
    private Integer height;

    @Schema(description = "Weight in kilograms")
    @Min(value = 0, message = "Wight must be positive")
    private Integer weight;

    @NotNull(message = "Gender is required")
    private  String gender;

    @NotNull(message = "Lifestyle is required")
    private Lifestyle lifestyle;

    @Schema(description = "External data source ID")
    private String dataSourceId;

    @Schema(description = "User ID in the external source")
    private String externalSourceUserId;

    private List<String> diseases;
}
