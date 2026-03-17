package com.intelligent_personal_trainer.data_processor_service.data_reader.csv;

import java.util.Map;

public record CsvSourceConfig(
        String sourceId,
        String filePath,
        String dateFormat,
        Map<String, String> mappings
) {

}
