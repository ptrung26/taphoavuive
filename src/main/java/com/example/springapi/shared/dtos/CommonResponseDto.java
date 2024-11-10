package com.example.springapi.shared.dtos;

public class CommonResponseDto<T> {
    public boolean isSuccess;
    public String message;
    public T data;

    public CommonResponseDto() {}

    public CommonResponseDto(boolean isSuccess, String message, T data) {

    }
}
