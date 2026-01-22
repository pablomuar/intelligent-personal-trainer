package com.intelligent_personal_trainer.trainer_service.repository;

import com.intelligent_personal_trainer.trainer_service.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByUserIdOrderByUpdatedAtDesc(String userId);
}
