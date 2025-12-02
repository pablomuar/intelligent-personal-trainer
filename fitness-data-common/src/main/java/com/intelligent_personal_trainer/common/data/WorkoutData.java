package com.intelligent_personal_trainer.common.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutData {

    private String workoutType;

    @Builder.Default
    private Map<String, String> attributes = new HashMap<>();

    public void addAttribute(String key, String value) {
        this.attributes.put(key, value);
    }
}
