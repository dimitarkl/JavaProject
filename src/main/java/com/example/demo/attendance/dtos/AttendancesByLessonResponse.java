package com.example.demo.attendance.dtos;

import lombok.Data;

import java.util.List;

@Data
public class AttendancesByLessonResponse {
    List<AttendanceResponse> attendances;
    int totalAttendances;
}
