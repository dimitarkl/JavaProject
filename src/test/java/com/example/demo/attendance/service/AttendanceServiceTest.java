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
import com.example.demo.subject.model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AttendanceService Unit Tests")
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private AttendanceMapper attendanceMapper;

    @Mock
    private LessonService lessonService;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private AttendanceService attendanceService;

    private UUID lessonId;
    private UUID studentId;
    private Lesson lesson;
    private Student student;
    private AttendanceData attendanceData;
    private Attendance attendance;
    private AttendanceResponse attendanceResponse;
    private AttendanceKey attendanceKey;

    @BeforeEach
    void setUp() {
        lessonId = UUID.randomUUID();
        studentId = UUID.randomUUID();

        Subject subject = Subject.builder()
                .id(UUID.randomUUID())
                .name("Mathematics")
                .build();

        lesson = Lesson.builder()
                .id(lessonId)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(1).plusHours(2))
                .subject(subject)
                .build();

        student = Student.builder()
                .id(studentId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        attendanceData = new AttendanceData();
        attendanceData.setLessonId(lessonId);
        attendanceData.setStudentId(studentId);

        attendanceKey = new AttendanceKey(lessonId, studentId);

        attendance = Attendance.builder()
                .id(attendanceKey)
                .lesson(lesson)
                .student(student)
                .registeredAt(LocalDateTime.now())
                .build();

        attendanceResponse = new AttendanceResponse(
                lessonId,
                studentId,
                "John",
                "Doe",
                "john.doe@example.com",
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Should record attendance successfully")
    void testRecordAttendance_Success() {
        when(lessonService.getLessonEntityById(lessonId)).thenReturn(lesson);
        when(studentService.getStudentEntityById(studentId)).thenReturn(student);
        when(attendanceRepository.findById(attendanceKey)).thenReturn(Optional.empty());
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance);
        when(attendanceMapper.toResponse(attendance)).thenReturn(attendanceResponse);

        AttendanceResponse result = attendanceService.recordAttendance(attendanceData);

        assertNotNull(result);
        assertEquals(lessonId, result.lessonId());
        assertEquals(studentId, result.studentId());
        assertEquals("John", result.studentFirstName());
        assertEquals("Doe", result.studentLastName());
        verify(attendanceRepository, times(1)).findById(attendanceKey);
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Should throw exception when attendance already recorded")
    void testRecordAttendance_AlreadyExists() {
        when(lessonService.getLessonEntityById(lessonId)).thenReturn(lesson);
        when(studentService.getStudentEntityById(studentId)).thenReturn(student);
        when(attendanceRepository.findById(attendanceKey)).thenReturn(Optional.of(attendance));

        assertThrows(IllegalStateException.class,
                () -> attendanceService.recordAttendance(attendanceData));

        verify(attendanceRepository, times(1)).findById(attendanceKey);
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Should get attendances by lesson ID successfully")
    void testGetAttendancesByLessonId_Success() {
        List<Attendance> attendances = List.of(attendance);
        when(attendanceRepository.findByLessonId(lessonId)).thenReturn(attendances);
        when(attendanceRepository.countByLessonId(lessonId)).thenReturn(1);
        when(attendanceMapper.toResponse(attendance)).thenReturn(attendanceResponse);

        AttendancesByLessonResponse result = attendanceService.getAttendancesByLessonId(lessonId);

        assertNotNull(result);
        assertEquals(1, result.getAttendances().size());
        assertEquals(1, result.getTotalAttendances());
        assertEquals(lessonId, result.getAttendances().get(0).lessonId());
        verify(attendanceRepository, times(1)).findByLessonId(lessonId);
        verify(attendanceRepository, times(1)).countByLessonId(lessonId);
    }

    @Test
    @DisplayName("Should return empty list when no attendances for lesson")
    void testGetAttendancesByLessonId_NoAttendances() {
        when(attendanceRepository.findByLessonId(lessonId)).thenReturn(List.of());
        when(attendanceRepository.countByLessonId(lessonId)).thenReturn(0);

        AttendancesByLessonResponse result = attendanceService.getAttendancesByLessonId(lessonId);

        assertNotNull(result);
        assertEquals(0, result.getAttendances().size());
        assertEquals(0, result.getTotalAttendances());
        verify(attendanceRepository, times(1)).findByLessonId(lessonId);
        verify(attendanceRepository, times(1)).countByLessonId(lessonId);
    }
}

