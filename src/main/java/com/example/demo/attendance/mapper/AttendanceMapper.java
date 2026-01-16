package com.example.demo.attendance.mapper;

import com.example.demo.attendance.dtos.AttendanceResponse;
import com.example.demo.attendance.model.Attendance;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {

    public AttendanceResponse toResponse(Attendance entity) {
        return new AttendanceResponse(
                entity.getId().getLessonId(),
                entity.getId().getStudentId(),
                entity.getStudent().getFirstName(),
                entity.getStudent().getLastName(),
                entity.getStudent().getEmail(),
                entity.getRegisteredAt()
        );
    }
}
