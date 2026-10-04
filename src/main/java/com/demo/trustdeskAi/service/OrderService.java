package com.demo.trustdeskAi.service;

import com.demo.trustdeskAi.enitities.OrderEntity;
import com.demo.trustdeskAi.repositories.OrderRepository;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    /**
     * Ingests order data from a JSON file in classpath (e.g., src/main/resources/data/orders.json)
     */
//    @Transactional
//    public void ingestOrdersFromJson(String resourcePath) {
//        log.info("Starting ingestion of order records from resource: {}", resourcePath);
//        try {
//            ClassPathResource resource = new ClassPathResource(resourcePath);
//            InputStream inputStream = resource.getInputStream();
//
//            List<OrderEntity> orders = objectMapper.readValue(
//                    inputStream,
//                    new TypeReference<List<OrderEntity>>() {}
//            );
//
//            orderRepository.saveAll(orders);
//            log.info("Successfully ingested {} orders into PostgreSQL.", orders.size());
//        } catch (Exception e) {
//            log.error("Failed to ingest order data from path: {}", resourcePath, e);
//            throw new RuntimeException("Error ingesting order records", e);
//        }
//    }

    /**
     * Seeds initial sample orders directly if running in a fresh environment
     */
    @Transactional
    public void seedSampleOrders() {
        if (orderRepository.count() > 0) {
            log.info("Orders table already populated. Skipping sample seeding.");
            return;
        }

        log.info("Seeding initial sample order records...");

        List<OrderEntity> sampleOrders = List.of(
                new OrderEntity(
                        "ORD-99281",
                        "CUST-101",
                        "user101@example.com",
                        LocalDateTime.of(2026, 8, 15, 10, 30),
                        LocalDateTime.of(2026, 8, 18, 14, 20),
                        "DELIVERED",
                        new BigDecimal("199.99"),
                        "[{\"sku\": \"HEADSET-PRO\", \"name\": \"Pro Wireless Headphones\", \"quantity\": 1}]"
                ),
                new OrderEntity(
                        "ORD-88210",
                        "CUST-102",
                        "user102@example.com",
                        LocalDateTime.of(2026, 9, 20, 9, 15),
                        LocalDateTime.of(2026, 9, 22, 11, 45),
                        "DELIVERED",
                        new BigDecimal("49.99"),
                        "[{\"sku\": \"CHARGER-FAST\", \"name\": \"65W USB-C Charger\", \"quantity\": 1}]"
                )
        );

        orderRepository.saveAll(sampleOrders);
        log.info("Successfully seeded {} sample orders.", sampleOrders.size());
    }

    @Transactional(readOnly = true)
    public OrderEntity getOrderById(String orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));
    }

    @Transactional(readOnly = true)
    public List<OrderEntity> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public long getOrderCount() {
        return orderRepository.count();
    }
}