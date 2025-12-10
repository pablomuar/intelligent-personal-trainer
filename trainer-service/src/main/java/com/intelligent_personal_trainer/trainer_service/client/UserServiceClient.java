package com.intelligent_personal_trainer.trainer_service.client;

import com.intelligent_personal_trainer.user_common.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${services.user.url}")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    User getUser(@PathVariable("userId") String userId);
}
