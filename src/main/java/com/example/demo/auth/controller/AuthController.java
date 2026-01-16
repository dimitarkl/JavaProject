package com.example.demo.auth.controller;

import com.example.demo.auth.dto.*;
import com.example.demo.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.refresh-token.cookie-name:refreshToken}")
    private String refreshTokenCookieName;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @PostMapping("/register/student")
    public ResponseEntity<AuthResponse> registerStudent(
            @Valid @RequestBody RegisterStudentRequest request,
            HttpServletResponse response) {
        AuthTokens tokens = authService.registerStudent(request);
        addRefreshTokenCookie(response, tokens.refreshToken);
        return ResponseEntity.ok(new AuthResponse(tokens.accessToken));
    }

    @PostMapping("/register/teacher")
    public ResponseEntity<AuthResponse> registerTeacher(
            @Valid @RequestBody RegisterTeacherRequest request,
            HttpServletResponse response) {
        AuthTokens tokens = authService.registerTeacher(request);
        addRefreshTokenCookie(response, tokens.refreshToken);
        return ResponseEntity.ok(new AuthResponse(tokens.accessToken));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        AuthTokens tokens = authService.login(request);
        addRefreshTokenCookie(response, tokens.refreshToken);
        return ResponseEntity.ok(new AuthResponse(tokens.accessToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = "${jwt.refresh-token.cookie-name:refreshToken}") String refreshToken,
            HttpServletResponse response) {
        AuthTokens tokens = authService.refresh(refreshToken);
        addRefreshTokenCookie(response, tokens.refreshToken);
        return ResponseEntity.ok(new AuthResponse(tokens.accessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        clearRefreshTokenCookie(response);
        return ResponseEntity.ok().build();
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(refreshTokenCookieName, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); //TODO Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge((int) (refreshTokenExpiration / 1000));
        response.addCookie(cookie);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(refreshTokenCookieName, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false); //TODO Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}