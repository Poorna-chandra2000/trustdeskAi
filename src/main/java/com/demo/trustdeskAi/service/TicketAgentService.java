package com.demo.trustdeskAi.service;



import com.demo.trustdeskAi.dtos.TicketEvaluationResultDto;
import com.demo.trustdeskAi.enitities.TicketEvaluationEntity;
import com.demo.trustdeskAi.repositories.TicketEvaluationRepository;
import com.demo.trustdeskAi.utils.enums.TicketStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketAgentService {

    private final ChatClient chatClient;


    private final TicketEvaluationRepository ticketRepository;

    // Inject the single pre-configured ChatClient instance
//    public TicketAgentService(ChatClient chatClient) {
//        this.chatClient = chatClient;
//    }

    public TicketEvaluationResultDto evaluateTicket(String conversationId, String ticketId, String issueDescription) {

        log.info("Evaluating ticket with ID: {} and issue: {}", ticketId, issueDescription);
        return chatClient.prompt()
                .user(String.format("Ticket ID: %s\nIssue: %s", ticketId, issueDescription))
                .advisors(spec -> spec.param("chat_memory_conversation_id", conversationId))
                .call()
                .entity(TicketEvaluationResultDto.class);
    }

    @Transactional(readOnly = true)
    public List<TicketEvaluationEntity> getPendingHumanApprovals() {
        log.info("Fetching all tickets pending human approval");
        return ticketRepository.findByStatusOrderByCreatedAtDesc(TicketStatus.PENDING_HUMAN_APPROVAL);
    }

    @Transactional(readOnly = true)
    public List<TicketEvaluationEntity> getPendingHumanApprovalsForConversation(String conversationId) {
        log.info("Fetching tickets pending human approval for conversation ID: {}", conversationId);
        return ticketRepository.findByStatusAndConversationIdOrderByCreatedAtDesc(
                TicketStatus.PENDING_HUMAN_APPROVAL,
                conversationId
        );
    }

    @Transactional
    public TicketEvaluationEntity processHumanReview(String ticketId, boolean approved, String overrideDecision, String reviewerNotes) {
        log.info("Processing human review for ticket ID: {}. Approved: {}", ticketId, approved);

        TicketEvaluationEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NoSuchElementException("Ticket not found with ID: " + ticketId));

        if (ticket.getStatus() != TicketStatus.PENDING_HUMAN_APPROVAL) {
            throw new IllegalStateException("Ticket " + ticketId + " is not pending human approval. Current status: " + ticket.getStatus());
        }

        if (approved) {
            ticket.setStatus(TicketStatus.APPROVED_BY_HUMAN);
            ticket.setFinalDecision(ticket.getProposedDecision());
            log.info("Human reviewer APPROVED AI proposed decision for ticket [{}]", ticketId);
        } else if (overrideDecision != null && !overrideDecision.isBlank()) {
            ticket.setStatus(TicketStatus.OVERRIDDEN_BY_HUMAN);
            ticket.setFinalDecision(overrideDecision);
            log.info("Human reviewer OVERRODE decision for ticket [{}] with: {}", ticketId, overrideDecision);
        } else {
            ticket.setStatus(TicketStatus.REJECTED);
            ticket.setFinalDecision("REJECTED: " + (reviewerNotes != null ? reviewerNotes : "No notes provided"));
            log.info("Human reviewer REJECTED AI proposal for ticket [{}]", ticketId);
        }

        ticket.setHumanReviewerNotes(reviewerNotes);
        ticket.setReviewedAt(LocalDateTime.now());

        return ticketRepository.save(ticket);
    }


    @Transactional
    public TicketEvaluationResultDto evaluateAndPersistTicket(String conversationId, String ticketId, String issueDescription) {

        TicketEvaluationResultDto result;

        try {
            // 1. Evaluate ticket using Spring AI
            result = chatClient.prompt()
                    .user(String.format("Ticket ID: %s\nIssue Description: %s", ticketId, issueDescription))
                    .advisors(spec -> spec.param("chat_memory_conversation_id", conversationId))
                    .call()
                    .entity(TicketEvaluationResultDto.class);

        } catch (Exception e) {
            log.error("❌ Exception during LLM evaluation/tool execution for ticket [{}]: ", ticketId, e);
            throw new IllegalStateException("Ticket evaluation failed: " + e.getMessage(), e);
        }

        if (result == null) {
            throw new IllegalStateException("Failed to evaluate ticket: Model returned empty response for ticket ID " + ticketId);
        }

        // 2. Map DTO to Hibernate Entity safely
        TicketEvaluationEntity entity = new TicketEvaluationEntity();

        String resolvedTicketId = (result.getTicketId() != null && !result.getTicketId().isBlank())
                ? result.getTicketId()
                : ticketId;

        entity.setTicketId(resolvedTicketId);
        entity.setConversationId(conversationId);
        entity.setIssueDescription(issueDescription);
        entity.setProposedDecision(result.getDecision());
        entity.setPriority(result.getPriority());
        entity.setReasoning(result.getReasoning());
        entity.setRequiresHumanApproval(result.isRequiresHumanApproval());
        entity.setCitedPolicyDoc(result.getCitedPolicyDoc());
        entity.setCreatedAt(LocalDateTime.now());

        if (result.isRequiresHumanApproval()) {
            entity.setStatus(TicketStatus.PENDING_HUMAN_APPROVAL);
            log.warn("🚨 Ticket [{}] flagged for human review. Priority: {}", resolvedTicketId, result.getPriority());
        } else {
            entity.setStatus(TicketStatus.AUTO_RESOLVED);
            entity.setFinalDecision(result.getDecision());
            log.info("✅ Ticket [{}] auto-resolved.", resolvedTicketId);
        }

        ticketRepository.save(entity);
        return result;
    }
//    @Transactional
//    public TicketEvaluationResultDto evaluateAndPersistTicket(String conversationId, String ticketId, String issueDescription) {
//
//        // 1. Evaluate with Gemini + PgVector
//        TicketEvaluationResultDto result = chatClient.prompt()
//                .user(String.format("Ticket ID: %s\nIssue: %s", ticketId, issueDescription))
//                .advisors(spec -> spec.param("chat_memory_conversation_id", conversationId))
//                .call()
//                .entity(TicketEvaluationResultDto.class);
//
//        // 2. Map to Entity & Determine Initial Status
//        TicketEvaluationEntity entity = new TicketEvaluationEntity();
//
//        entity.setTicketId(Objects.requireNonNull(result).getTicketId());
//        entity.setConversationId(conversationId);
//        entity.setIssueDescription(issueDescription);
//        entity.setProposedDecision(result.getDecision());
//        entity.setPriority(result.getPriority());
//        entity.setReasoning(result.getReasoning());
//        entity.setRequiresHumanApproval(result.isRequiresHumanApproval());
//        entity.setCitedPolicyDoc(result.getCitedPolicyDoc());
//        entity.setCreatedAt(LocalDateTime.now());
//
//        if (result.isRequiresHumanApproval()) {
//            entity.setStatus(TicketStatus.PENDING_HUMAN_APPROVAL);
//            log.warn("🚨 Ticket [{}] flagged for human review. Priority: {}", ticketId, result.getPriority());
//        } else {
//            entity.setStatus(TicketStatus.AUTO_RESOLVED);
//            entity.setFinalDecision(result.getDecision());
//            log.info("✅ Ticket [{}] auto-resolved.", ticketId);
//        }
//
//        ticketRepository.save(entity);
//        return result;
//    }


    public record TicketEvaluationResult(
            String ticketId,
            String decision,
            String priority,
            String reasoning,
            boolean requiresHumanApproval,
            String citedPolicyDoc
    ) {}
}