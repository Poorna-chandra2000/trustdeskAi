package com.demo.trustdeskAi.enitities;


import com.demo.trustdeskAi.utils.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "ticket_evaluations")
public class TicketEvaluationEntity {

    @Id
    private String ticketId;

    private String conversationId;

    @Column(columnDefinition = "TEXT")
    private String issueDescription;

    @Column(columnDefinition = "TEXT")
    private String proposedDecision;

    private String priority;

    @Column(columnDefinition = "TEXT")
    private String reasoning;

    private boolean requiresHumanApproval;
    private String citedPolicyDoc;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Column(columnDefinition = "TEXT")
    private String finalDecision;

    @Column(columnDefinition = "TEXT")
    private String humanReviewerNotes;

    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
}