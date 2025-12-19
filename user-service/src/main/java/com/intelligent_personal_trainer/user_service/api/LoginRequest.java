package com.intelligent_personal_trainer.user_service.api;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
