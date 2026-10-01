package com.demo.trustdeskAi.dtos;


public class TicketRequestDto {
    private String ticketId;
    private String issueDescription;

    // Default constructor
    public TicketRequestDto() {}

    // Parameterized constructor
    public TicketRequestDto(String ticketId, String issueDescription) {
        this.ticketId = ticketId;
        this.issueDescription = issueDescription;
    }

    // Getters and Setters
    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }
}
