package com.intelligent_personal_trainer.data_persistence_service;

import com.intelligent_personal_trainer.common.data.FitnessData;
import com.intelligent_personal_trainer.data_persistence_service.mapper.FitnessDataMapper;
import com.intelligent_personal_trainer.data_persistence_service.repository.FitnessDataJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FitnessDataPersistenceService {
    private final FitnessDataJpaRepository jpaRepository;
    private final FitnessDataMapper fitnessDataMapper;

    @Transactional
    public void processAndSave(FitnessData fitnessData) {
        jpaRepository.save(fitnessDataMapper.toEntity(fitnessData));
        log.debug("Data of user {} saved successfully", fitnessData.getUserId());
    }
}
