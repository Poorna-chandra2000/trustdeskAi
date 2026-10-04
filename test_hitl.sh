#!/usr/bin/env bash

# Adjust BASE_URL if your application uses a context-path or different port
BASE_URL="http://localhost:8083/api/v1"

echo "=================================================="
echo "      TRUSTDESK AI - END-TO-END SYSTEM TEST      "
echo "=================================================="
echo ""

# --------------------------------------------------
# PART 1: ORDERS CONTROLLER ENDPOINTS
# --------------------------------------------------

echo "--------------------------------------------------"
echo "1. Seed Sample Orders (/ingest-orders)"
echo "--------------------------------------------------"
curl -s -X POST "${BASE_URL}/ingest-orders"
echo -e "\n"

echo "--------------------------------------------------"
echo "2. Bulk Ingest Custom Order (/orders/bulk)"
echo "--------------------------------------------------"
curl -s -X POST "${BASE_URL}/orders/bulk" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "orderId": "ORD-3001",
      "customerId": "CUST-301",
      "customerEmail": "dave@example.com",
      "purchaseDate": "2026-09-25T10:00:00",
      "deliveryDate": "2026-09-28T14:00:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 199.99,
      "itemsJson": "[{\"sku\": \"4K-WEBCAM-PRO\", \"name\": \"4K Ultra HD Webcam\", \"quantity\": 1}]"
    }
  ]'
echo -e "\n"

echo "--------------------------------------------------"
echo "3. Get Total Ingested Order Count (/orders/count)"
echo "--------------------------------------------------"
curl -s -X GET "${BASE_URL}/orders/count"
echo -e "\n"

echo "--------------------------------------------------"
echo "4. Fetch All Orders (/orders)"
echo "--------------------------------------------------"
curl -s -X GET "${BASE_URL}/orders"
echo -e "\n"

echo "--------------------------------------------------"
echo "5. Fetch Order by ID (/orders/ORD-3001)"
echo "--------------------------------------------------"
curl -s -X GET "${BASE_URL}/orders/ORD-3001"
echo -e "\n"

echo "--------------------------------------------------"
echo "6. Fetch Orders by Customer ID (/orders/customer/CUST-301)"
echo "--------------------------------------------------"
curl -s -X GET "${BASE_URL}/orders/customer/CUST-301"
echo -e "\n"


# --------------------------------------------------
# PART 2: TICKET AGENT CONTROLLER (HITL FLOW)
# --------------------------------------------------

echo "--------------------------------------------------"
echo "7. Ingest Policy Markdown Files (/agent/ingest-policies)"
echo "--------------------------------------------------"
curl -s -X POST "${BASE_URL}/agent/ingest-policies"
echo -e "\n"

echo "--------------------------------------------------"
echo "8. Evaluate Ticket to Trigger HITL Queue (TCK-3001)"
echo "--------------------------------------------------"
curl -s -X POST "${BASE_URL}/agent/evaluate-ticket" \
  -H "Content-Type: application/json" \
  -H "X-Conversation-Id: conv-cust-301" \
  -d '{
    "ticketId": "TCK-3001",
    "issueDescription": "I bought a 4K Ultra HD Webcam under order ORD-3001 delivered 3 days ago. It does not fit my setup and I want to return it for a refund."
  }'
echo -e "\n"

echo "--------------------------------------------------"
echo "9. Fetch All Global Pending HITL Approvals (/agent/pending-approvals)"
echo "--------------------------------------------------"
curl -s -X GET "${BASE_URL}/agent/pending-approvals"
echo -e "\n"

echo "--------------------------------------------------"
echo "10. Fetch User Pending HITL Approvals (/agent/pending-approvals/user)"
echo "--------------------------------------------------"
curl -s -X GET "${BASE_URL}/agent/pending-approvals/user" \
  -H "X-Conversation-Id: conv-cust-301"
echo -e "\n"

echo "--------------------------------------------------"
echo "11. Submit Human Review Decision -> APPROVE TCK-3001"
echo "--------------------------------------------------"
curl -s -X POST "${BASE_URL}/agent/tickets/TCK-3001/human-review" \
  -H "Content-Type: application/json" \
  -d '{
    "approved": true,
    "reviewerNotes": "Approved return request within 7-day policy window."
  }'
echo -e "\n"

echo "--------------------------------------------------"
echo "12. Verify Pending Queue is Cleared (/agent/pending-approvals)"
echo "--------------------------------------------------"
curl -s -X GET "${BASE_URL}/agent/pending-approvals"
echo -e "\n"

echo "=================================================="
echo "            ALL ENDPOINT TESTS COMPLETED          "
echo "=================================================="