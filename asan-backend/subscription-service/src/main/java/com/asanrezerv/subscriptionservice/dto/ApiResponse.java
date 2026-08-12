package com.asanrezerv.subscriptionservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private T data;
    private boolean success;

    public static  <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(data,true);
    }


    public static <T> ApiResponse<T> fail(T error){
        return new ApiResponse<>(error,false);
    }

}
