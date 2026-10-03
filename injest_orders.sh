#!/bin/bash

BASE_URL="http://localhost:8083/api/v1"

echo "=========================================="
echo "Ingesting 10 Orders with Distinct Customers"
echo "=========================================="

curl -s -X POST "${BASE_URL}/orders/bulk" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "orderId": "ORD-1001",
      "customerId": "CUST-201",
      "customerEmail": "alice@example.com",
      "purchaseDate": "2026-08-10T10:15:00",
      "deliveryDate": "2026-08-13T14:30:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 120.50,
      "itemsJson": "[{\"sku\": \"WIRELESS-MOUSE\", \"name\": \"Ergonomic Wireless Mouse\", \"qty\": 1}]"
    },
    {
      "orderId": "ORD-1002",
      "customerId": "CUST-202",
      "customerEmail": "bob@example.com",
      "purchaseDate": "2026-08-15T09:00:00",
      "deliveryDate": "2026-08-18T11:00:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 299.99,
      "itemsJson": "[{\"sku\": \"MECH-KEYBOARD\", \"name\": \"RGB Mechanical Keyboard\", \"qty\": 1}]"
    },
    {
      "orderId": "ORD-1003",
      "customerId": "CUST-203",
      "customerEmail": "charlie@example.com",
      "purchaseDate": "2026-08-20T16:20:00",
      "deliveryDate": "2026-08-23T15:10:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 45.00,
      "itemsJson": "[{\"sku\": \"USB-HUB-4PORT\", \"name\": \"4-Port USB-C Hub\", \"qty\": 1}]"
    },
    {
      "orderId": "ORD-1004",
      "customerId": "CUST-204",
      "customerEmail": "david@example.com",
      "purchaseDate": "2026-08-25T11:45:00",
      "deliveryDate": "2026-08-28T10:00:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 89.99,
      "itemsJson": "[{\"sku\": \"HD-WEBCAM\", \"name\": \"1080p HD Webcam\", \"qty\": 1}]"
    },
    {
      "orderId": "ORD-1005",
      "customerId": "CUST-205",
      "customerEmail": "eva@example.com",
      "purchaseDate": "2026-09-01T08:30:00",
      "deliveryDate": "2026-09-04T13:15:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 150.00,
      "itemsJson": "[{\"sku\": \"DESK-LAMP-LED\", \"name\": \"Smart LED Desk Lamp\", \"qty\": 1}]"
    },
    {
      "orderId": "ORD-1006",
      "customerId": "CUST-206",
      "customerEmail": "frank@example.com",
      "purchaseDate": "2026-09-05T14:10:00",
      "deliveryDate": "2026-09-08T16:40:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 35.00,
      "itemsJson": "[{\"sku\": \"MONITOR-ARM\", \"name\": \"Single Monitor Mount\", \"qty\": 1}]"
    },
    {
      "orderId": "ORD-1007",
      "customerId": "CUST-207",
      "customerEmail": "grace@example.com",
      "purchaseDate": "2026-09-10T12:00:00",
      "deliveryDate": "2026-09-13T09:30:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 550.00,
      "itemsJson": "[{\"sku\": \"4K-MONITOR\", \"name\": \"27-inch 4K Monitor\", \"qty\": 1}]"
    },
    {
      "orderId": "ORD-1008",
      "customerId": "CUST-208",
      "customerEmail": "hannah@example.com",
      "purchaseDate": "2026-09-15T17:05:00",
      "deliveryDate": "2026-09-18T12:20:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 25.00,
      "itemsJson": "[{\"sku\": \"CABLE-HDMI-6FT\", \"name\": \"High Speed HDMI Cable 6ft\", \"qty\": 2}]"
    },
    {
      "orderId": "ORD-1009",
      "customerId": "CUST-209",
      "customerEmail": "ian@example.com",
      "purchaseDate": "2026-09-20T10:40:00",
      "deliveryDate": "2026-09-23T14:00:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 199.00,
      "itemsJson": "[{\"sku\": \"NOISE-CANCEL-EARBUDS\", \"name\": \"Wireless Earbuds Pro\", \"qty\": 1}]"
    },
    {
      "orderId": "ORD-1010",
      "customerId": "CUST-210",
      "customerEmail": "julia@example.com",
      "purchaseDate": "2026-09-25T15:30:00",
      "deliveryDate": "2026-09-28T11:15:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 79.50,
      "itemsJson": "[{\"sku\": \"LAPTOP-STAND-ALU\", \"name\": \"Aluminum Laptop Stand\", \"qty\": 1}]"
    }
  ]'

echo -e "\n"

echo "=========================================="
echo "1. Verify Total Ingested Order Count"
echo "=========================================="
curl -s -X GET "${BASE_URL}/orders/count"
echo -e "\n"

echo "=========================================="
echo "2. Fetch All Ingested Orders"
echo "=========================================="
curl -s -X GET "${BASE_URL}/orders"
echo -e "\n"

echo "=========================================="
echo "3. Fetch Specific Order (ORD-1001)"
echo "=========================================="
curl -s -X GET "${BASE_URL}/orders/ORD-1001"
echo -e "\n"

echo "=========================================="
echo "4. Fetch Orders for Customer (CUST-201)"
echo "=========================================="
curl -s -X GET "${BASE_URL}/orders/customer/CUST-201"
echo -e "\n"


#!/usr/bin/env bash

# ==============================================================================
# TrustDesk AI - Seed Ingestion Script
# ==============================================================================
# Ingests 10 OrderEntity objects into PostgreSQL via /api/v1/orders/bulk,
# vectorizes policies, and submits 10 support ticket scenarios (Auto-Approved,
# High Priority HITL, Low Priority Policy Exceptions, Fraud Alerts, etc.).
# ==============================================================================

BASE_URL="http://localhost:8083/api/v1"

echo "=========================================================="
echo "🚀 TrustDesk AI: Starting Bulk Order & Ticket Ingestion"
echo "=========================================================="

# ------------------------------------------------------------------------------
# 1. BULK INGEST 10 ORDER ENTITIES
# ------------------------------------------------------------------------------
echo -e "\n1️⃣ Bulk Ingesting 10 Order Entities into PostgreSQL..."

curl -s -X POST "${BASE_URL}/orders/bulk" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "orderId": "ORD-3001",
      "customerId": "CUST-101",
      "customerEmail": "alex.johnson@example.com",
      "purchaseDate": "2026-03-20T10:30:00",
      "deliveryDate": "2026-03-23T14:15:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 199.99,
      "itemsJson": "[{\"sku\":\"CAM-4K\",\"name\":\"4K Ultra HD Pro Webcam\",\"qty\":1,\"price\":199.99}]"
    },
    {
      "orderId": "ORD-3002",
      "customerId": "CUST-102",
      "customerEmail": "sara.connor@example.com",
      "purchaseDate": "2026-03-25T09:00:00",
      "deliveryDate": "2026-03-28T11:45:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 1899.00,
      "itemsJson": "[{\"sku\":\"LAP-900\",\"name\":\"Pro Gaming Laptop RTX4080\",\"qty\":1,\"price\":1899.00}]"
    },
    {
      "orderId": "ORD-3003",
      "customerId": "CUST-103",
      "customerEmail": "michael.scott@example.com",
      "purchaseDate": "2026-03-26T16:20:00",
      "deliveryDate": "2026-03-29T10:10:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 149.50,
      "itemsJson": "[{\"sku\":\"EAR-NC\",\"name\":\"Noise Cancelling Wireless Earbuds\",\"qty\":1,\"price\":149.50}]"
    },
    {
      "orderId": "ORD-3004",
      "customerId": "CUST-104",
      "customerEmail": "ellen.ripley@example.com",
      "purchaseDate": "2026-01-10T08:15:00",
      "deliveryDate": "2026-01-14T13:00:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 299.00,
      "itemsJson": "[{\"sku\":\"WTH-PRO\",\"name\":\"Smart Fitness Watch Series 5\",\"qty\":1,\"price\":299.00}]"
    },
    {
      "orderId": "ORD-3005",
      "customerId": "CUST-105",
      "customerEmail": "bruce.wayne@example.com",
      "purchaseDate": "2026-03-27T18:00:00",
      "deliveryDate": "2026-03-30T15:30:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 129.99,
      "itemsJson": "[{\"sku\":\"KBD-MECH\",\"name\":\"RGB Mechanical Gaming Keyboard\",\"qty\":1,\"price\":129.99}]"
    },
    {
      "orderId": "ORD-3006",
      "customerId": "CUST-106",
      "customerEmail": "diana.prince@example.com",
      "purchaseDate": "2026-03-28T11:10:00",
      "deliveryDate": "2026-03-31T09:20:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 1499.00,
      "itemsJson": "[{\"sku\":\"DRN-4K\",\"name\":\"4K Aerial Cine Drone Bundle\",\"qty\":1,\"price\":1499.00}]"
    },
    {
      "orderId": "ORD-3007",
      "customerId": "CUST-107",
      "customerEmail": "pete.mitchell@example.com",
      "purchaseDate": "2026-03-22T14:45:00",
      "deliveryDate": "2026-03-25T16:00:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 89.95,
      "itemsJson": "[{\"sku\":\"HUB-USB\",\"name\":\"7-in-1 USB-C Thunderbolt Hub\",\"qty\":1,\"price\":89.95}]"
    },
    {
      "orderId": "ORD-3008",
      "customerId": "CUST-108",
      "customerEmail": "tony.stark@example.com",
      "purchaseDate": "2026-03-29T08:00:00",
      "deliveryDate": "2026-04-01T12:00:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 3499.00,
      "itemsJson": "[{\"sku\":\"MON-OLED\",\"name\":\"49 Ultra-Wide Curved OLED Monitor\",\"qty\":1,\"price\":3499.00}]"
    },
    {
      "orderId": "ORD-3009",
      "customerId": "CUST-109",
      "customerEmail": "natasha.r@example.com",
      "purchaseDate": "2026-03-24T13:30:00",
      "deliveryDate": "2026-03-27T10:45:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 79.00,
      "itemsJson": "[{\"sku\":\"CHG-MAG\",\"name\":\"3-in-1 Magnetic Fast Charger\",\"qty\":1,\"price\":79.00}]"
    },
    {
      "orderId": "ORD-3010",
      "customerId": "CUST-110",
      "customerEmail": "clark.kent@example.com",
      "purchaseDate": "2026-02-01T10:00:00",
      "deliveryDate": "2026-02-05T14:00:00",
      "orderStatus": "DELIVERED",
      "totalAmount": 450.00,
      "itemsJson": "[{\"sku\":\"AUD-DAC\",\"name\":\"High-Res Desktop DAC Audio Amplifier\",\"qty\":1,\"price\":450.00}]"
    }
  ]'

echo -e "\n✅ Bulk Orders Ingested."

# ------------------------------------------------------------------------------
# 2. INGEST POLICIES
# ------------------------------------------------------------------------------
echo -e "\n2️⃣ Ingesting Policy Documents into Vector Database..."
curl -s -X POST "${BASE_URL}/agent/ingest-policies"
echo -e "\n✅ Policies Vectorized."

# ------------------------------------------------------------------------------
# 3. SUBMIT 10 SUPPORT TICKET EVALUATION SCENARIOS
# ------------------------------------------------------------------------------
echo -e "\n3️⃣ Evaluating 10 Support Ticket Scenarios..."
echo "----------------------------------------------------------"

submit_ticket() {
  local tck="$1"
  local conv="$2"
  local desc="$3"
  echo -e "\n--> Submitting Ticket ${tck}..."
  curl -s -X POST "${BASE_URL}/agent/evaluate-ticket" \
    -H "Content-Type: application/json" \
    -H "X-Conversation-Id: ${conv}" \
    -d "{
      \"conversationId\": \"${conv}\",
      \"ticketId\": \"${tck}\",
      \"issueDescription\": \"${desc}\"
    }" | grep -o '"requiresHumanApproval":[^,]*'
}

# 1. Standard Return (Auto-Approved)
submit_ticket "TCK-3001" "conv-cust-101" "I bought a 4K Ultra HD Pro Webcam under order ORD-3001 delivered 3 days ago. It does not fit my setup and I want to return it for a full refund."

# 2. High Value Damage - HIGH PRIORITY (HITL Pending)
submit_ticket "TCK-3002" "conv-cust-102" "HIGH PRIORITY: My Pro Gaming Laptop (\$1,899.00) under order ORD-3002 arrived today with a shattered screen. I need an immediate full refund or priority replacement."

# 3. Defective Replacement (Auto-Approved)
submit_ticket "TCK-3003" "conv-cust-103" "The Noise Cancelling Earbuds in order ORD-3003 delivered yesterday will not charge at all. Requesting a replacement pair under standard warranty."

# 4. Out-of-Window Policy Exception - LOW PRIORITY (HITL Pending)
submit_ticket "TCK-3004" "conv-cust-104" "LOW PRIORITY: I bought a Smart Fitness Watch under order ORD-3004 over 80 days ago. I know the 30-day window passed, but can I get store credit exception as a loyal customer?"

# 5. Wrong Item Delivered (Auto-Approved)
submit_ticket "TCK-3005" "conv-cust-105" "Under order ORD-3005, I received a mouse pad instead of the RGB Mechanical Keyboard I ordered. Package is unopened."

# 6. High-Value Fraud/Stolen Item - HIGH PRIORITY (HITL Pending)
submit_ticket "TCK-3006" "conv-cust-106" "HIGH PRIORITY: Order ORD-3006 ($1,499.00 Aerial Drone) was marked delivered by carrier, but my porch camera shows no driver arrived. Reporting missing package."

# 7. Defective Hub Exchange (Auto-Approved)
submit_ticket "TCK-3007" "conv-cust-107" "The USB-C Hub in order ORD-3007 keeps disconnecting HDMI during calls. Requesting a replacement."

# 8. High Value Curved OLED Monitor Damage - HIGH PRIORITY (HITL Pending)
submit_ticket "TCK-3008" "conv-cust-108" "HIGH PRIORITY: Opened order ORD-3008 (\$3,499 OLED Monitor) and screen has deep cracks right out of the box. Urgent replacement required."

# 9. Simple Refund Inquiry (Auto-Approved)
submit_ticket "TCK-3009" "conv-cust-109" "Order ORD-3009 Magnetic Charger returned in original packaging within 5 days. Requesting full refund process."

# 10. Out-of-Warranty Repair Exception - MEDIUM PRIORITY (HITL Pending)
submit_ticket "TCK-3010" "conv-cust-110" "MEDIUM PRIORITY: Audio DAC from order ORD-3010 purchased 2 months ago has a flickering LED screen. Requesting partial repair credit or replacement exchange."

echo -e "\n=========================================================="
echo "🎉 Seed Ingestion Completed Successfully!"
echo "Open TrustDesk Dashboard at http://localhost:8080"
echo "=========================================================="