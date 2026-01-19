package com.intelligent_personal_trainer.trainer_service.repository;

import com.intelligent_personal_trainer.trainer_service.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    List<ChatHistory> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(String userId, LocalDateTime from, LocalDateTime to);
}
