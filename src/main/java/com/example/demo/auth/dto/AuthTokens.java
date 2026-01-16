package com.example.demo.auth.dto;

public class AuthTokens {
    public final String accessToken;
    public final String refreshToken;

    public AuthTokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}