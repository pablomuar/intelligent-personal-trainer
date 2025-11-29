package com.intelligent_personal_trainer.data_processor_service.data_reader;

import com.intelligent_personal_trainer.common.data.FitnessData;

import java.time.LocalDate;
import java.util.List;

public interface FitnessDataReader {

    /**
     * @param sourceId El ID definido en el JSON
     * @param userId El ID del usuario a filtrar
     * @param date La fecha específica a extraer
     * @return Lista de FitnessData para el usuario y la fecha especificados
     */
    List<FitnessData> readData(String sourceId, String userId, LocalDate date);
}