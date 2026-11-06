package com.mounika.ecommerce_backend.controller;

import com.mounika.ecommerce_backend.dto.OrderResponse;
import com.mounika.ecommerce_backend.dto.OrderStatusUpdateRequest;
import com.mounika.ecommerce_backend.service.OrderService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PutMapping("/{orderId}/status")
    public OrderResponse updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusUpdateRequest request) {

        return orderService.updateOrderStatus(
                orderId, request.getStatus()
        );
    }
}