package com.example.demo.subject.dto;

import java.util.UUID;

public record SubjectRequest(
        String name,
        Integer maxAttendance,
        UUID teacherId
) {}