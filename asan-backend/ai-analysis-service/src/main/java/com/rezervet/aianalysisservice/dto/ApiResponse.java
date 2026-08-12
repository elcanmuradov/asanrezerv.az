package com.rezervet.aianalysisservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private T data;
    private boolean success;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, true);
    }

    public static <T> ApiResponse<T> fail(T error) {
        return new ApiResponse<>(error, false);
    }
}
