package com.example.demo.teacher.dto;

public record TeacherRequest(
        String firstName,
        String lastName,
        String email
) {}