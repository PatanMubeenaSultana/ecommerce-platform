package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.dto.UserResponse;
import com.ecommerce.user_service.response.ApiResponse;
import com.ecommerce.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ProtectedController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> profile(){

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "profile fetched successfully",
                        userService.getProfile()
                )
        );
    }
}
