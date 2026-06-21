package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.dto.RegisterRequest;
import com.ecommerce.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public String register(
            @RequestBody RegisterRequest request) {

        return userService.registerUser(request);
    }
}
