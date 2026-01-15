package com.example.demo.faculty.dto;

import java.util.UUID;

public record FacultyRequest(
        String name,
        String email,
        String phone
) {}
