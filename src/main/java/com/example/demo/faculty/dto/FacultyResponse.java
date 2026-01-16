package com.example.demo.faculty.dto;

import java.util.UUID;

public record FacultyResponse(
        UUID id,
        String name,
        String email,
        String phone
) {}
