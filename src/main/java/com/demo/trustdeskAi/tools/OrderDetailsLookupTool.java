package com.demo.trustdeskAi.tools;

import com.demo.trustdeskAi.enitities.OrderEntity;
import com.demo.trustdeskAi.repositories.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
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
            @ToolParam(description = "The order ID extracted from the ticket (e.g. 'ORD-99281')", required = true)
            String orderId,

            @ToolParam(description = "The created_at timestamp of the ticket being evaluated (ISO format: YYYY-MM-DDTHH:MM:SS)", required = true)
            String ticketCreatedAtStr) {

        log.info("🔧 [TOOL CALL] Fetching database facts for Order ID: {}", orderId);

        Optional<OrderEntity> orderOpt = orderRepository.findByOrderId(orderId);

        if (orderOpt.isEmpty()) {
            return String.format("Order ID '%s' was not found in the database.", orderId);
        }

        OrderEntity order = orderOpt.get();
        LocalDateTime ticketCreatedAt = LocalDateTime.parse(ticketCreatedAtStr);

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
                - Ticket Created At: %s
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
    }
}