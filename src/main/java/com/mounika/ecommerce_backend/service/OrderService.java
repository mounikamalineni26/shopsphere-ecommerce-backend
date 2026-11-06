package com.mounika.ecommerce_backend.service;

import com.mounika.ecommerce_backend.dto.OrderItemRequest;
import com.mounika.ecommerce_backend.dto.PlaceOrderRequest;
import com.mounika.ecommerce_backend.entity.Order;
import com.mounika.ecommerce_backend.entity.OrderItem;
import com.mounika.ecommerce_backend.dto.OrderResponse;
import com.mounika.ecommerce_backend.dto.OrderItemResponse;
import java.util.stream.Collectors;
import com.mounika.ecommerce_backend.entity.Product;
import com.mounika.ecommerce_backend.entity.User;
import com.mounika.ecommerce_backend.enums.OrderStatus;
import com.mounika.ecommerce_backend.repository.*;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.mounika.ecommerce_backend.entity.Cart;
import com.mounika.ecommerce_backend.entity.CartItem;
import com.mounika.ecommerce_backend.repository.CartRepository;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository, CartRepository cartRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
    }

    private OrderResponse convertToResponse(Order order) {

        List<OrderItemResponse> items = order.getOrderItems()
                .stream()
                .map(item -> {

                    BigDecimal subtotal = item.getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));

                    return new OrderItemResponse(
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            item.getQuantity(),
                            item.getPrice(),
                            subtotal
                    );
                })
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                items
        );
    }

    @Transactional
    public OrderResponse placeOrder(String email, PlaceOrderRequest request) {

        // 1. Find the authenticated customer
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        // 2. Validate the order request
        if (request == null || request.getItems() == null
                || request.getItems().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Order must contain items");
        }

        List<Product> products = new ArrayList<>();
        Set<Long> productIds = new HashSet<>();

        // 3. Validate all products and available stock
        for (OrderItemRequest item : request.getItems()) {

            if (item.getProductId() == null || item.getQuantity() == null
                    || item.getQuantity() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid product or quantity");
            }

            if (!productIds.add(item.getProductId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Duplicate product IDs are not allowed");
            }

            Product product = productRepository
                    .findById(item.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Product not found"));

            if (product.getQuantity() < item.getQuantity()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Insufficient stock for " + product.getName());
            }

            products.add(product);
        }

        // 4. Create the order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 5. Create order items and calculate total
        for (int i = 0; i < request.getItems().size(); i++) {

            OrderItemRequest itemRequest = request.getItems().get(i);
            Product product = products.get(i);

            BigDecimal unitPrice = BigDecimal.valueOf(product.getPrice());

            BigDecimal subtotal = unitPrice.multiply(
                    BigDecimal.valueOf(itemRequest.getQuantity()));

            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(unitPrice);

            order.getOrderItems().add(orderItem);

            // 6. Reduce available stock
            product.setQuantity(
                    product.getQuantity() - itemRequest.getQuantity());
        }

        // 7. Set total amount
        order.setTotalAmount(totalAmount);

        // 8. Save order (OrderItems are saved through cascade)
        Order savedOrder = orderRepository.save(order);

        return convertToResponse(savedOrder);
    }
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String email) {

        // 1. Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        ));

        // 2. Fetch only this user's orders
        List<Order> orders =
                orderRepository.findByUserId(user.getId());

        // 3. Convert entities into response DTOs
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String email, Long orderId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        Order order = orderRepository.findByIdAndUserId(
                orderId, user.getId()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Order not found"));

        return convertToResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(String email, Long orderId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        Order order = orderRepository.findByIdAndUserId(
                orderId, user.getId()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only pending orders can be cancelled"
            );
        }

        for (OrderItem item : order.getOrderItems()) {

            Product product = item.getProduct();

            product.setQuantity(
                    product.getQuantity() + item.getQuantity()
            );
        }

        order.setStatus(OrderStatus.CANCELLED);

        return convertToResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId, OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order not found"));

        OrderStatus currentStatus = order.getStatus();

        boolean validTransition = switch (currentStatus) {
            case PENDING ->
                    newStatus == OrderStatus.CONFIRMED ||
                            newStatus == OrderStatus.CANCELLED;

            case CONFIRMED ->
                    newStatus == OrderStatus.SHIPPED ||
                            newStatus == OrderStatus.CANCELLED;

            case SHIPPED ->
                    newStatus == OrderStatus.DELIVERED;

            case DELIVERED, CANCELLED -> false;
        };

        if (!validTransition) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid order status transition"
            );
        }

        order.setStatus(newStatus);

        return convertToResponse(order);
    }

    @Transactional
    public OrderResponse checkout(String email) {

        // 1. Find the customer
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        // 2. Find customer's cart
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Cart is empty"));

        // 3. Check whether cart has items
        if (cart.getItems().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Cannot checkout an empty cart");
        }

        // 4. Convert CartItems into OrderItemRequests
        List<OrderItemRequest> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {

            OrderItemRequest request = new OrderItemRequest();

            request.setProductId(cartItem.getProduct().getId());
            request.setQuantity(cartItem.getQuantity());

            orderItems.add(request);
        }

        // 5. Create PlaceOrderRequest
        PlaceOrderRequest placeOrderRequest = new PlaceOrderRequest();
        placeOrderRequest.setItems(orderItems);

        // 6. Reuse existing order placement logic
        OrderResponse orderResponse =
                placeOrder(email, placeOrderRequest);

        // 7. Clear cart after successful order creation
        cart.getItems().clear();

        cartRepository.save(cart);

        // 8. Return order details
        return orderResponse;
    }
}