package com.ecommerce.user_service.exception;

public class ProductServiceUnavailableException extends RuntimeException{
    public ProductServiceUnavailableException(String message) {
        super(message);
    }
}
