package com.rezervet.aianalysisservice.exception;

import com.rezervet.aianalysisservice.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AiException.class)
    public ResponseEntity<ApiResponse<String>> handleAi(AiException e) {
        return ResponseEntity.status(e.getStatus()).body(ApiResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneric(Exception e) {
        return ResponseEntity.status(500).body(ApiResponse.fail("Analiz xətası: " + e.getMessage()));
    }
}
