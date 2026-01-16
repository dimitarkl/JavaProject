package com.example.demo.exceptions;

public record ErrorResponse(
        String error,
        String message
) {
}
