
package com.mounika.ecommerce_backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {

    private Long cartId;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;

    public CartResponse() {
    }

    public CartResponse(
            Long cartId,
            List<CartItemResponse> items,
            BigDecimal totalAmount) {

        this.cartId = cartId;
        this.items = items;
        this.totalAmount = totalAmount;
    }

    public Long getCartId() {
        return cartId;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
