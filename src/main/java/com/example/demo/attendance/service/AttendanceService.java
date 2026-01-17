package com.example.demo.attendance.service;

import com.example.demo.attendance.dtos.AttendanceData;
import com.example.demo.attendance.dtos.AttendanceResponse;
import com.example.demo.attendance.dtos.AttendancesByLessonResponse;
import com.example.demo.attendance.mapper.AttendanceMapper;
import com.example.demo.attendance.model.Attendance;
import com.example.demo.attendance.model.AttendanceKey;
import com.example.demo.attendance.repository.AttendanceRepository;
import com.example.demo.lesson.model.Lesson;
import com.example.demo.lesson.service.LessonService;
import com.example.demo.student.model.Student;
import com.example.demo.student.service.StudentService;
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

    private final LessonService lessonService;
    private final StudentService studentService;

    @Transactional
    public AttendanceResponse recordAttendance(AttendanceData attendanceData) {

        Lesson lesson = lessonService.getLessonEntityById(attendanceData.getLessonId());
        Student student = studentService.getStudentEntityById(attendanceData.getStudentId());

        AttendanceKey key = new AttendanceKey(
                attendanceData.getLessonId(),
                attendanceData.getStudentId()
        );

        attendanceRepository.findById(key).ifPresent(attendance -> {
            throw new IllegalStateException("Attendance already recorded for this lesson");
        });

        Attendance attendance = new Attendance();
        attendance.setId(key);
        attendance.setLesson(lesson);
        attendance.setStudent(student);
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
