package com.demo.trustdeskAi.repositories;

import com.demo.trustdeskAi.enitities.TicketEvaluationEntity;
import com.demo.trustdeskAi.utils.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketEvaluationRepository extends JpaRepository<TicketEvaluationEntity, String> {
    List<TicketEvaluationEntity> findByStatusOrderByCreatedAtDesc(TicketStatus ticketStatus);

    List<TicketEvaluationEntity> findByStatusAndConversationIdOrderByCreatedAtDesc(TicketStatus ticketStatus, String conversationId);
}
