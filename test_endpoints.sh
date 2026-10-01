#!/bin/bash

BASE_URL="http://localhost:8083/api/v1/agent"
CONVERSATION_ID="conv-12345"
TICKET_ID="TCK-1001"

echo "=========================================="
echo "1. Ingest Policy Markdown Files"
echo "=========================================="
curl -X POST "${BASE_URL}/ingest-policies"
echo -e "\n\n"

echo "=========================================="
echo "2. Evaluate Ticket"
echo "=========================================="
curl -X POST "${BASE_URL}/evaluate-ticket" \
  -H "Content-Type: application/json" \
  -H "X-Conversation-Id: ${CONVERSATION_ID}" \
  -d "{
    \"ticketId\": \"${TICKET_ID}\",
    \"issueDescription\": \"User is requesting a full refund after 45 days, which exceeds the standard 30-day return policy.\"
  }"
echo -e "\n\n"

echo "=========================================="
echo "3. Get All Pending Approvals (Global)"
echo "=========================================="
curl -X GET "${BASE_URL}/pending-approvals"
echo -e "\n\n"

echo "=========================================="
echo "4. Get Pending Approvals for User Conversation"
echo "=========================================="
curl -X GET "${BASE_URL}/pending-approvals/user" \
  -H "X-Conversation-Id: ${CONVERSATION_ID}"
echo -e "\n\n"

echo "=========================================="
echo "5a. Submit Human Review - Approve AI Decision"
echo "=========================================="
curl -X POST "${BASE_URL}/tickets/${TICKET_ID}/human-review" \
  -H "Content-Type: application/json" \
  -d '{
    "approved": true,
    "overrideDecision": null,
    "reviewerNotes": "Approved based on policy compliance."
  }'
echo -e "\n\n"

echo "=========================================="
echo "5b. Submit Human Review - Override AI Decision"
echo "=========================================="
curl -X POST "${BASE_URL}/tickets/${TICKET_ID}/human-review" \
  -H "Content-Type: application/json" \
  -d '{
    "approved": false,
    "overrideDecision": "Issue 50% store credit as a goodwill gesture.",
    "reviewerNotes": "Overridden due to customer loyalty exception."
  }'
echo -e "\n"