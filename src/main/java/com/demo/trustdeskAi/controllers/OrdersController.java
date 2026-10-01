package com.demo.trustdeskAi.controllers;

import com.demo.trustdeskAi.enitities.OrderEntity;
import com.demo.trustdeskAi.repositories.OrderRepository;
import com.demo.trustdeskAi.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
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
}
