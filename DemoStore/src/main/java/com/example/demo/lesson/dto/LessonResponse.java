package com.example.demo.lesson.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LessonResponse(
        UUID id,
        String subjectName,
        String teacherName,
        LocalDateTime startDate,
        LocalDateTime endDate
) {}