package com.mounika.ecommerce_backend.repository;

import com.mounika.ecommerce_backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find all orders placed by a particular user
    List<Order> findByUserId(Long userId);

    // Find one order belonging to a particular user
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);
}