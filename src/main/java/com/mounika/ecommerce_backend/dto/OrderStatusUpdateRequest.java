package com.mounika.ecommerce_backend.dto;
import com.mounika.ecommerce_backend.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
public class OrderStatusUpdateRequest {

    @NotNull(message = "Order status is required")
    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}