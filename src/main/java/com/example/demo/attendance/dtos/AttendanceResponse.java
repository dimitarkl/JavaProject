package com.example.demo.attendance.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record AttendanceResponse(
        UUID lessonId,
        UUID studentId,
        String studentFirstName,
        String studentLastName,
        String studentEmail,
        LocalDateTime registeredAt
) {}
