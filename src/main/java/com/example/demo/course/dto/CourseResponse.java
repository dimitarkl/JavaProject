package com.example.demo.course.dto;

import java.util.UUID;

public record CourseResponse(
        UUID id,
        String name,
        UUID facultyId,
        String facultyName
) {}
