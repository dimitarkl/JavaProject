package com.example.demo.lesson.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LessonRequest(
        UUID subjectId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String type
) {}