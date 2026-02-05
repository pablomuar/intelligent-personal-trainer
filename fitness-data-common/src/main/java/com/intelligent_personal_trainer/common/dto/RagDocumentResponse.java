package com.intelligent_personal_trainer.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagDocumentResponse {
    private String content;
    private Map<String, Object> metadata;
}
