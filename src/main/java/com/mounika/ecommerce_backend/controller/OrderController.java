
package com.mounika.ecommerce_backend.controller;

import com.mounika.ecommerce_backend.dto.PlaceOrderRequest;
import com.mounika.ecommerce_backend.dto.OrderResponse;
import com.mounika.ecommerce_backend.service.OrderService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Place order directly using request body
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(
            @Valid @RequestBody PlaceOrderRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return orderService.placeOrder(email, request);
    }

    // Checkout using cart items
    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse checkout(Authentication authentication) {

        String email = authentication.getName();

        return orderService.checkout(email);
    }

    // Get all orders of logged-in customer
    @GetMapping
    public List<OrderResponse> getMyOrders(
            Authentication authentication) {

        String email = authentication.getName();

        return orderService.getMyOrders(email);
    }

    // Get specific order
    @GetMapping("/{orderId}")
    public OrderResponse getOrderById(
            @PathVariable Long orderId,
            Authentication authentication) {

        String email = authentication.getName();

        return orderService.getOrderById(email, orderId);
    }

    // Cancel order
    @PutMapping("/{orderId}/cancel")
    public OrderResponse cancelOrder(
            @PathVariable Long orderId,
            Authentication authentication) {

        return orderService.cancelOrder(
                authentication.getName(), orderId);
    }
}
