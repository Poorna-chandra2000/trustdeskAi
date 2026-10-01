package com.demo.trustdeskAi.dtos;

import lombok.Getter;

@Getter
public class HumanReviewRequestDto {
    private boolean approved;
    private String overrideDecision;
    private String reviewerNotes;

    // Default constructor
    public HumanReviewRequestDto() {}

    // Parameterized constructor
    public HumanReviewRequestDto(boolean approved, String overrideDecision, String reviewerNotes) {
        this.approved = approved;
        this.overrideDecision = overrideDecision;
        this.reviewerNotes = reviewerNotes;
    }

    // Getters and Setters
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getOverrideDecision() {
        return overrideDecision;
    }

    public void setOverrideDecision(String overrideDecision) {
        this.overrideDecision = overrideDecision;
    }

    public String getReviewerNotes() {
        return reviewerNotes;
    }

    public void setReviewerNotes(String reviewerNotes) {
        this.reviewerNotes = reviewerNotes;
    }
}
