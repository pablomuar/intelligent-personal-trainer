package com.intelligent_personal_trainer.trainer_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
class TrainerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainerServiceApplication.class, args);
    }

}
