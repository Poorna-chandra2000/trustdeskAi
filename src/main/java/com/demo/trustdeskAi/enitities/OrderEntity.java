package com.demo.trustdeskAi.enitities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class OrderEntity {

    @Id
    private String orderId;

    private String customerId;
    private String customerEmail;
    private LocalDateTime purchaseDate;
    private LocalDateTime deliveryDate;
    private String orderStatus; // DELIVERED, SHIPPED, CANCELLED, REFUNDED
    private BigDecimal totalAmount;
    private String itemsJson;   // JSON string storing purchased items list
}