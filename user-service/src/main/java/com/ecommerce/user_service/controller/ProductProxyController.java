package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.dto.ProductResponse;
import com.ecommerce.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ProductProxyController {

    private final UserService userService;

    @GetMapping("/products/{productId}")
    public ProductResponse getProductDetails(@PathVariable Long productId){
        return userService.getProductDetails(productId);
    }
}
