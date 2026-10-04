package com.demo.trustdeskAi.controllers;

import com.demo.trustdeskAi.enitities.OrderEntity;
import com.demo.trustdeskAi.repositories.OrderRepository;
import com.demo.trustdeskAi.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class OrdersController {


    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @PostMapping("/ingest-orders")
    public ResponseEntity<String> ingestOrders() {
        orderService.seedSampleOrders(); // Or orderService.ingestOrdersFromJson("data/orders.json");
        return ResponseEntity.ok("Orders successfully ingested into PostgreSQL.");
    }

    @PostMapping("/orders/bulk")
    public ResponseEntity<String> bulkIngestOrders(@RequestBody List<OrderEntity> orders) {
        orderRepository.saveAll(orders);
        return ResponseEntity.ok(String.format("Successfully ingested %d orders into PostgreSQL.", orders.size()));
    }

    // 1. Get total ingested order count
    @GetMapping("/orders/count")
    public ResponseEntity<Map<String, Object>> getOrderCount() {
        long count = orderService.getOrderCount();
        return ResponseEntity.ok(Map.of(
                "totalOrders", count,
                "status", "SUCCESS"
        ));
    }

    // 2. Fetch all ingested orders
    @GetMapping("/orders")
    public ResponseEntity<List<OrderEntity>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // 3. Fetch specific order details by Order ID
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderEntity> getOrderById(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // 4. Fetch orders associated with a Customer ID
    @GetMapping("/orders/customer/{customerId}")
    public ResponseEntity<List<OrderEntity>> getOrdersByCustomer(@PathVariable String customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }
}
