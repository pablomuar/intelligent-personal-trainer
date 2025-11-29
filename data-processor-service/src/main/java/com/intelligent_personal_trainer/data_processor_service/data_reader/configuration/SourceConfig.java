package com.intelligent_personal_trainer.data_processor_service.data_reader.configuration;

import java.util.Map;

public record SourceConfig(String sourceId, String timeZone, String filePath, String dateFormat, Map<String, String> mappings) {

}
