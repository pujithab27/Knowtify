package com.knowtify.controller;

import com.knowtify.entity.User;
import com.knowtify.service.UserService;
import com.knowtify.dto.LoginRequest;
import com.knowtify.dto.RegisterRequest;
import com.knowtify.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.createUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getFullName()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFullName(),
                    "User registered successfully",
                    user.getPreferredDomains() != null ? user.getPreferredDomains() : java.util.List.of()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthResponse(null, null, null, null, e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.getUserByUsername(request.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Simple password check (in production, use bcrypt)
            if (!user.getPassword().equals(request.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, null, null, null, "Invalid credentials"));
            }

            userService.updateLastActivityTime(user.getId());

            return ResponseEntity.ok(new AuthResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFullName(),
                    "Login successful",
                    user.getPreferredDomains() != null ? user.getPreferredDomains() : java.util.List.of()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, null, null, null, e.getMessage(), null));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        return userService.getUserById(userId)
                .map(user -> ResponseEntity.ok(new AuthResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getFullName(),
                        "User found",
                        user.getPreferredDomains() != null ? user.getPreferredDomains() : java.util.List.of()
                )))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AuthResponse(null, null, null, null, "User not found", null)));
    }
}
