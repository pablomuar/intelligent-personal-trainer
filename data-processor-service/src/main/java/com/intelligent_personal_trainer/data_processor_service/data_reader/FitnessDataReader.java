package com.intelligent_personal_trainer.data_processor_service.data_reader;

import com.intelligent_personal_trainer.common.data.FitnessData;

import java.time.LocalDate;
import java.util.List;

public interface FitnessDataReader {

    /**
     * @return true si el lector soporta este sourceId, false en caso contrario
     */
    boolean supportsSource(String sourceId);

    /**
     * @param sourceId El ID definido en el JSON
     * @param userId El ID del usuario a filtrar
     * @param externalSourceUserId El ID del usuario en la fuente externa
     * @param date La fecha específica a extraer
     * @return Lista de FitnessData para el usuario y la fecha especificados
     */
    List<FitnessData> readData(String sourceId, String userId, String externalSourceUserId, LocalDate date);
}