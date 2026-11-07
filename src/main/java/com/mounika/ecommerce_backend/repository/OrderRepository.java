
package com.mounika.ecommerce_backend.repository;

import com.mounika.ecommerce_backend.entity.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find all orders placed by a particular user
    List<Order> findByUserId(Long userId);

    // Find one order belonging to a particular user
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    // Find and lock an order during cancellation
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :orderId AND o.user.id = :userId")
    Optional<Order> findByIdAndUserIdForUpdate(
            @Param("orderId") Long orderId,
            @Param("userId") Long userId
    );
}
