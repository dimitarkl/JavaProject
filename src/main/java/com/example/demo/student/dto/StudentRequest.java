package com.example.demo.student.dto;

import java.util.UUID;

public record StudentRequest(
        String firstName,
        String lastName,
        String email,
        UUID courseId
) {}
