package com.rezervet.searchservice.exception;

import com.rezervet.searchservice.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(404).body(ApiResponse.fail(e.getMessage()));
    }
}
