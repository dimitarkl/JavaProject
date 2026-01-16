package com.example.demo.attendance.service;

import com.example.demo.attendance.dtos.AttendanceData;
import com.example.demo.attendance.dtos.AttendanceResponse;
import com.example.demo.attendance.dtos.AttendancesByLessonResponse;
import com.example.demo.attendance.mapper.AttendanceMapper;
import com.example.demo.attendance.model.Attendance;
import com.example.demo.attendance.model.AttendanceKey;
import com.example.demo.attendance.repository.AttendanceRepository;
import com.example.demo.lesson.repository.LessonRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper attendanceMapper;

    //TODO Remove this in the final product
    private final LessonRepository lessonRepository;

    @Transactional
    public AttendanceResponse recordAttendance(AttendanceData attendanceData) {
        //TODO make the lesson have max capacity and check in here if its full

        //TODO remove when lesson is integrated
        var lesson = lessonRepository.findById(attendanceData.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found"));

        AttendanceKey key = new AttendanceKey(
                attendanceData.getLessonId(),
                attendanceData.getStudentId()
        );

        attendanceRepository.findById(key).ifPresent(attendance -> {
            throw new IllegalStateException("Attendance already recorded for this lesson");
        });

        Attendance attendance = new Attendance();
        attendance.setId(key);
        attendance.setRegisteredAt(LocalDateTime.now());

        Attendance saved = attendanceRepository.save(attendance);

        return attendanceMapper.toResponse(saved);
    }

    public AttendancesByLessonResponse getAttendancesByLessonId(UUID lessonId){
        List<Attendance> attendances = attendanceRepository.findByLessonId(lessonId);

        AttendancesByLessonResponse response = new AttendancesByLessonResponse();
        response.setAttendances(
            attendances.stream()
                .map(attendanceMapper::toResponse)
                .toList()
        );
        response.setTotalAttendances(attendanceRepository.countByLessonId(lessonId));

        return response;
    }

}
