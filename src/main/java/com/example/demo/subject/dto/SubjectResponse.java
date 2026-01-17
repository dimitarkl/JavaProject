package com.example.demo.subject.dto;

import java.util.UUID;

public record SubjectResponse(
        UUID id,
        String name,
        Integer maxAttendance,
        String teacherName
) {}