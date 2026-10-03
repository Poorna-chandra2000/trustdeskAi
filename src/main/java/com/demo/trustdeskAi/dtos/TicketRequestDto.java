package com.demo.trustdeskAi.dtos;

public class TicketRequestDto {
    private String ticketId;
    private String conversationId;
    private String issueDescription;

    public TicketRequestDto() {}

    public TicketRequestDto(String ticketId, String conversationId, String issueDescription) {
        this.ticketId = ticketId;
        this.conversationId = conversationId;
        this.issueDescription = issueDescription;
    }

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }
}