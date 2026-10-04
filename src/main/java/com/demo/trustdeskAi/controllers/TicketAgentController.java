package com.demo.trustdeskAi.controllers;

import com.demo.trustdeskAi.dtos.HumanReviewRequestDto;
import com.demo.trustdeskAi.dtos.TicketEvaluationResultDto;
import com.demo.trustdeskAi.dtos.TicketRequestDto;
import com.demo.trustdeskAi.enitities.TicketEvaluationEntity;
import com.demo.trustdeskAi.service.MarkdownIngestionService;
import com.demo.trustdeskAi.service.TicketAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/agent")
@RequiredArgsConstructor
public class TicketAgentController {

    private final MarkdownIngestionService ingestionService;
    private final TicketAgentService agentService;

    @PostMapping("/ingest-policies")
    public ResponseEntity<String> ingestPolicies() throws IOException {
        ingestionService.ingestAllPolicyMarkdownFiles();
        return ResponseEntity.ok("All policy Markdown files successfully ingested into PgVector.");
    }

    @PostMapping("/evaluate-ticket")
    public ResponseEntity<TicketEvaluationResultDto> evaluateTicket(
            @RequestHeader(value = "X-Conversation-Id", required = false) String conversationHeader,
            @RequestBody TicketRequestDto request) {

        String conversationId = (conversationHeader != null && !conversationHeader.isBlank())
                ? conversationHeader
                : (request.getConversationId() != null && !request.getConversationId().isBlank()
                ? request.getConversationId()
                : "default-conv");

        TicketEvaluationResultDto result = agentService.evaluateAndPersistTicket(
                conversationId,
                request.getTicketId(),
                request.getIssueDescription()
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/pending-approvals")
    public ResponseEntity<List<TicketEvaluationEntity>> getPendingApprovals() {
        return ResponseEntity.ok(agentService.getPendingHumanApprovals());
    }

    @GetMapping("/pending-approvals/user")
    public ResponseEntity<List<TicketEvaluationEntity>> getPendingApprovalsForUser(
            @RequestHeader(value = "X-Conversation-Id", required = false) String conversationHeader,
            @RequestParam(value = "conversationId", required = false) String conversationParam) {

        String conversationId = (conversationHeader != null && !conversationHeader.isBlank())
                ? conversationHeader
                : conversationParam;

        if (conversationId == null || conversationId.isBlank()) {
            return ResponseEntity.ok(agentService.getPendingHumanApprovals());
        }

        return ResponseEntity.ok(agentService.getPendingHumanApprovalsForConversation(conversationId));
    }

    @PostMapping("/tickets/{ticketId}/human-review")
    public ResponseEntity<TicketEvaluationEntity> submitHumanReview(
            @PathVariable String ticketId,
            @RequestBody HumanReviewRequestDto reviewRequest) {

        TicketEvaluationEntity updatedTicket = agentService.processHumanReview(
                ticketId,
                reviewRequest.isApproved(),
                reviewRequest.getOverrideDecision(),
                reviewRequest.getReviewerNotes()
        );

        return ResponseEntity.ok(updatedTicket);
    }
}