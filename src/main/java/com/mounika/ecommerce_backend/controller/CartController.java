
package com.mounika.ecommerce_backend.controller;

import com.mounika.ecommerce_backend.dto.AddToCartRequest;
import com.mounika.ecommerce_backend.dto.UpdateCartRequest;
import com.mounika.ecommerce_backend.dto.CartResponse;
import com.mounika.ecommerce_backend.service.CartService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Add product to cart
    @PostMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    public String addToCart(
            @Valid @RequestBody AddToCartRequest request,
            Authentication authentication) {

        cartService.addToCart(authentication.getName(), request);

        return "Product added to cart successfully";
    }

    // View cart
    @GetMapping
    public CartResponse getCart(Authentication authentication) {
        return cartService.getCart(authentication.getName());
    }

    // Update product quantity
    @PutMapping("/items/{productId}")
    public CartResponse updateQuantity(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartRequest request,
            Authentication authentication) {

        return cartService.updateQuantity(
                authentication.getName(),
                productId,
                request.getQuantity()
        );
    }

    // Remove one product
    @DeleteMapping("/items/{productId}")
    public String removeItem(
            @PathVariable Long productId,
            Authentication authentication) {

        cartService.removeItem(authentication.getName(), productId);

        return "Product removed from cart successfully";
    }

    // Clear entire cart
    @DeleteMapping
    public String clearCart(Authentication authentication) {

        cartService.clearCart(authentication.getName());

        return "Cart cleared successfully";
    }
}
