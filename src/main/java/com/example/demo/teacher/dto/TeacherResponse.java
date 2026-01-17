package com.example.demo.teacher.dto;

import java.util.UUID;

public record TeacherResponse(
        UUID id,
        String firstName,
        String lastName,
        String email
) {}