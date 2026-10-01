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