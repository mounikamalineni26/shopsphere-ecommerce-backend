package com.mounika.ecommerce_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class PlaceOrderRequest {

    @NotEmpty(message = "Order must contain at least one item")
    private List<@Valid OrderItemRequest> items;

    public PlaceOrderRequest() {
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}