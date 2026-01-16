package com.example.demo.course.dto;

import java.util.UUID;

public record CourseRequest(
        String name,
        UUID facultyId
) {}
