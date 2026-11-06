
package com.mounika.ecommerce_backend.repository;

import com.mounika.ecommerce_backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndProductId(
            Long cartId, Long productId
    );
}
