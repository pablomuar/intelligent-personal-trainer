package com.intelligent_personal_trainer.data_persistence_service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Data Persistence Service API", description = "API documentation for the Data Persistence Service", version = "1.0.0"))
public class DataPersistenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataPersistenceApplication.class, args);
    }
}
