package com.mounika.ecommerce_backend.repository;

import com.mounika.ecommerce_backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    // Find all items belonging to one order
    List<OrderItem> findByOrderId(Long orderId);
}