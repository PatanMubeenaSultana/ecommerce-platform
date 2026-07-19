package com.ecommerce.user_service.exception;

import com.ecommerce.user_service.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Map<String,String>> handleValidationException(MethodArgumentNotValidException ex){

        Map<String,String> errors = new HashMap<>();

        for(FieldError error: ex.getFieldErrors())
        {
            errors.put(
                    error.getField(),
                    error.getDefaultMessage()
            );
        }

        return new ApiResponse<>(
                false,
                "Validation Failed",
                errors
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ApiResponse<String> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex) {

        return new ApiResponse<>(
                false,
                ex.getMessage(),
                null
        );
    }
}
