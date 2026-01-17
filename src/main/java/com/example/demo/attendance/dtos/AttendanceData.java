package com.example.demo.attendance.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class AttendanceData {
    private UUID lessonId;
    private UUID studentId;
}
