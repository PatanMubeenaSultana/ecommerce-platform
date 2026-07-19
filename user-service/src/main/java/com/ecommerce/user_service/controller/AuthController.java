package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.dto.LoginRequest;
import com.ecommerce.user_service.dto.LoginResponse;
import com.ecommerce.user_service.dto.RegisterRequest;
import com.ecommerce.user_service.response.ApiResponse;
import com.ecommerce.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        System.out.println("Inside Register Controller");
        ApiResponse<String> response = new ApiResponse<>(
                true,
                userService.registerUser(request),
                null
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        return userService.loginUser(request);
    }
}
