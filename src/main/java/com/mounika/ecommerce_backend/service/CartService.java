
package com.mounika.ecommerce_backend.service;

import com.mounika.ecommerce_backend.dto.AddToCartRequest;
import com.mounika.ecommerce_backend.dto.CartItemResponse;
import com.mounika.ecommerce_backend.dto.CartResponse;
import com.mounika.ecommerce_backend.entity.*;
import com.mounika.ecommerce_backend.repository.CartRepository;
import com.mounika.ecommerce_backend.repository.ProductRepository;
import com.mounika.ecommerce_backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // Helper: Find logged-in user
    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));
    }

    // Helper: Find user's cart
    private Cart findCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cart not found"));
    }

    // 1. Add product to cart
    @Transactional
    public void addToCart(String email, AddToCartRequest request) {

        User user = findUser(email);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(new Cart(user)));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"));

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId()
                        .equals(product.getId()))
                .findFirst()
                .orElse(null);

        int newQuantity = request.getQuantity();

        if (existingItem != null) {
            newQuantity += existingItem.getQuantity();
        }

        if (newQuantity > product.getQuantity()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Insufficient stock available");
        }

        if (existingItem != null) {
            existingItem.setQuantity(newQuantity);
        } else {
            CartItem cartItem = new CartItem(cart, product, newQuantity);
            cart.getItems().add(cartItem);
        }

        cartRepository.save(cart);
    }

    // 2. View cart
    @Transactional(readOnly = true)
    public CartResponse getCart(String email) {

        User user = findUser(email);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElse(null);

        // Return an empty cart if the customer has not added anything
        if (cart == null) {
            return new CartResponse(
                    null, new ArrayList<>(), BigDecimal.ZERO);
        }

        List<CartItemResponse> responseItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {

            Product product = item.getProduct();

            BigDecimal unitPrice =
                    BigDecimal.valueOf(product.getPrice());

            BigDecimal subtotal =
                    unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

            CartItemResponse response = new CartItemResponse(
                    product.getId(),
                    product.getName(),
                    unitPrice,
                    item.getQuantity(),
                    subtotal
            );

            responseItems.add(response);
            totalAmount = totalAmount.add(subtotal);
        }

        return new CartResponse(
                cart.getId(),
                responseItems,
                totalAmount
        );
    }

    // 3. Update product quantity
    @Transactional
    public CartResponse updateQuantity(
            String email, Long productId, Integer quantity) {

        User user = findUser(email);
        Cart cart = findCart(user.getId());

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId()
                        .equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found in cart"));

        Product product = cartItem.getProduct();

        if (quantity > product.getQuantity()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Insufficient stock available");
        }

        cartItem.setQuantity(quantity);

        cartRepository.save(cart);

        return getCart(email);
    }

    // 4. Remove one product from cart
    @Transactional
    public void removeItem(String email, Long productId) {

        User user = findUser(email);
        Cart cart = findCart(user.getId());

        boolean removed = cart.getItems().removeIf(
                item -> item.getProduct().getId().equals(productId));

        if (!removed) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product not found in cart");
        }

        cartRepository.save(cart);
    }

    // 5. Clear entire cart
    @Transactional
    public void clearCart(String email) {

        User user = findUser(email);
        Cart cart = findCart(user.getId());

        cart.getItems().clear();

        cartRepository.save(cart);
    }
}
