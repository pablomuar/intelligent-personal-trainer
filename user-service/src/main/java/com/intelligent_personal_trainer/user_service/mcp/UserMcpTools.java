package com.intelligent_personal_trainer.user_service.mcp;

import com.intelligent_personal_trainer.user_common.User;
import com.intelligent_personal_trainer.user_service.UserService;
import lombok.RequiredArgsConstructor;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class UserMcpTools {

    private final UserService userService;

    @McpTool(name = "getUserProfile", description = "Retrieves user profile information (age, weight, height, diseases, lifestyle, etc.) given their ID.")
    public User getUserProfile(
            @McpToolParam(description = "The unique user identifier (UUID)") String userId
    ) {
        return userService.getUser(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));
    }
}