package com.rezervet.reservationservice.exception;

import com.rezervet.reservationservice.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFoundException(NotFoundException e){
        return ResponseEntity.status(404).body(ApiResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        return ResponseEntity.status(400).body(ApiResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(ReservationException.class)
    public ResponseEntity<ApiResponse<String>> handleReachedQuotaLimitException(ReservationException e){
        return ResponseEntity.status(400).body(ApiResponse.fail(e.getMessage()));
    }
}
