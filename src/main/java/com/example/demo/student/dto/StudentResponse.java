package com.example.demo.student.dto;

import java.util.UUID;

public record StudentResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        UUID courseId,
        String courseName
) {}
