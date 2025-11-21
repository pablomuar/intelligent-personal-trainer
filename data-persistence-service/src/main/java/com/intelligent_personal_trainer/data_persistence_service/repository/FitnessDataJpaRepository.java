package com.intelligent_personal_trainer.data_persistence_service.repository;

import com.intelligent_personal_trainer.data_persistence_service.entity.FitnessDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FitnessDataJpaRepository extends JpaRepository<FitnessDataEntity, Long> {

}
