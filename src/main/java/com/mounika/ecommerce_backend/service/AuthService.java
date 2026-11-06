package com.mounika.ecommerce_backend.service;

import com.mounika.ecommerce_backend.dto.AuthResponse;
import com.mounika.ecommerce_backend.dto.LoginRequest;
import com.mounika.ecommerce_backend.dto.LoginResponse;
import com.mounika.ecommerce_backend.dto.RegisterRequest;
import com.mounika.ecommerce_backend.entity.User;
import com.mounika.ecommerce_backend.repository.UserRepository;
import com.mounika.ecommerce_backend.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // Registration
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Never store a plain-text password
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // Public registration always creates a CUSTOMER
        user.setRole("CUSTOMER");

        User savedUser = userRepository.save(user);

        return new AuthResponse(
                "Registration successful",
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    // Login and JWT generation
    public LoginResponse login(LoginRequest request) {

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(),
                                    request.getPassword()
                            )
                    );

            UserDetails userDetails =
                    (UserDetails) authentication.getPrincipal();

            String token = jwtService.generateToken(userDetails);

            String role = userDetails.getAuthorities()
                    .stream()
                    .findFirst()
                    .map(authority ->
                            authority.getAuthority().replace("ROLE_", ""))
                    .orElse("CUSTOMER");

            return new LoginResponse(
                    token,
                    "Bearer",
                    userDetails.getUsername(),
                    role
            );

        } catch (BadCredentialsException ex) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }
    }
}