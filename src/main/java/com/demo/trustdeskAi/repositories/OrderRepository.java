package com.demo.trustdeskAi.repositories;

import com.demo.trustdeskAi.enitities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {

    // Find order by ID
    Optional<OrderEntity> findByOrderId(String orderId);

    // Find all orders for a specific customer
    List<OrderEntity> findByCustomerId(String customerId);

    // Find all orders associated with a customer's email
    List<OrderEntity> findByCustomerEmail(String customerEmail);
}