package com.mounika.ecommerce_backend.controller;

import com.mounika.ecommerce_backend.dto.AuthResponse;
import com.mounika.ecommerce_backend.dto.LoginRequest;
import com.mounika.ecommerce_backend.dto.LoginResponse;
import com.mounika.ecommerce_backend.dto.RegisterRequest;
import com.mounika.ecommerce_backend.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }
}