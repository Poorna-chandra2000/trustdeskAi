package com.demo.trustdeskAi.tools;

import com.demo.trustdeskAi.enitities.OrderEntity;
import com.demo.trustdeskAi.repositories.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Slf4j
@Component
public class OrderDetailsLookupTool {

    private final OrderRepository orderRepository;

    public OrderDetailsLookupTool(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Tool(description = "Fetches purchase date, delivery status, items, and elapsed return days for a given Order ID.")
    public String getOrderDetails(
            @ToolParam(description = "The order ID extracted from the ticket (e.g. 'ORD-1001')", required = true)
            String orderId,

            @ToolParam(description = "Optional created_at timestamp of the ticket (e.g. '2026-10-01' or '2026-10-01T10:00:00')", required = false)
            String ticketCreatedAtStr) {

        log.info("🔧 [TOOL CALL] Fetching database facts for Order ID: {}", orderId);

        try {
            Optional<OrderEntity> orderOpt = orderRepository.findByOrderId(orderId);

            if (orderOpt.isEmpty()) {
                return String.format("Order ID '%s' was not found in the database.", orderId);
            }

            OrderEntity order = orderOpt.get();
            LocalDateTime ticketCreatedAt = parseTimestamp(ticketCreatedAtStr);

            long daysElapsedSinceDelivery = 0;
            if (order.getDeliveryDate() != null) {
                daysElapsedSinceDelivery = Duration.between(order.getDeliveryDate(), ticketCreatedAt).toDays();
            }

            return String.format("""
                    [Order System Record for %s]
                    - Customer ID: %s
                    - Customer Email: %s
                    - Purchase Date: %s
                    - Delivery Date: %s
                    - Order Status: %s
                    - Total Amount: $%s
                    - Items Purchased: %s
                    - Ticket Created At Reference: %s
                    - Days Elapsed From Delivery To Ticket Creation: %d days
                    """,
                    order.getOrderId(),
                    order.getCustomerId(),
                    order.getCustomerEmail(),
                    order.getPurchaseDate(),
                    order.getDeliveryDate(),
                    order.getOrderStatus(),
                    order.getTotalAmount(),
                    order.getItemsJson(),
                    ticketCreatedAt,
                    daysElapsedSinceDelivery
            );
        } catch (Exception e) {
            log.error("Error executing OrderDetailsLookupTool for orderId: {}", orderId, e);
            return String.format("Order lookup performed for %s, but date calculation encountered an error: %s", orderId, e.getMessage());
        }
    }

    private LocalDateTime parseTimestamp(String timestampStr) {
        if (timestampStr == null || timestampStr.isBlank()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(timestampStr);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(timestampStr).atStartOfDay();
            } catch (DateTimeParseException ex) {
                return LocalDateTime.now();
            }
        }
    }
}


//package com.demo.trustdeskAi.tools;
//
//import com.demo.trustdeskAi.enitities.OrderEntity;
//import com.demo.trustdeskAi.repositories.OrderRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.ai.tool.annotation.Tool;
//import org.springframework.ai.tool.annotation.ToolParam;
//import org.springframework.stereotype.Component;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@Slf4j
//@Component
//public class OrderDetailsLookupTool {
//
//    private final OrderRepository orderRepository;
//
//    public OrderDetailsLookupTool(OrderRepository orderRepository) {
//        this.orderRepository = orderRepository;
//    }
//
//    @Tool(description = "Fetches purchase date, delivery status, items, and elapsed return days for a given Order ID.")
//    public String getOrderDetails(
//            @ToolParam(description = "The order ID extracted from the ticket (e.g. 'ORD-99281')", required = true)
//            String orderId,
//
//            @ToolParam(description = "The created_at timestamp of the ticket being evaluated (ISO format: YYYY-MM-DDTHH:MM:SS)", required = true)
//            String ticketCreatedAtStr) {
//
//        log.info("🔧 [TOOL CALL] Fetching database facts for Order ID: {}", orderId);
//
//        Optional<OrderEntity> orderOpt = orderRepository.findByOrderId(orderId);
//
//        if (orderOpt.isEmpty()) {
//            return String.format("Order ID '%s' was not found in the database.", orderId);
//        }
//
//        OrderEntity order = orderOpt.get();
//        LocalDateTime ticketCreatedAt = LocalDateTime.parse(ticketCreatedAtStr);
//
//        long daysElapsedSinceDelivery = 0;
//        if (order.getDeliveryDate() != null) {
//            daysElapsedSinceDelivery = Duration.between(order.getDeliveryDate(), ticketCreatedAt).toDays();
//        }
//
//        return String.format("""
//                [Order System Record for %s]
//                - Customer ID: %s
//                - Customer Email: %s
//                - Purchase Date: %s
//                - Delivery Date: %s
//                - Order Status: %s
//                - Total Amount: $%s
//                - Items Purchased: %s
//                - Ticket Created At: %s
//                - Days Elapsed From Delivery To Ticket Creation: %d days
//                """,
//                order.getOrderId(),
//                order.getCustomerId(),
//                order.getCustomerEmail(),
//                order.getPurchaseDate(),
//                order.getDeliveryDate(),
//                order.getOrderStatus(),
//                order.getTotalAmount(),
//                order.getItemsJson(),
//                ticketCreatedAt,
//                daysElapsedSinceDelivery
//        );
//    }
//}

