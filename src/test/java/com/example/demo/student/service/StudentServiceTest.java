package com.example.demo.student.service;

import com.example.demo.student.dto.StudentRequest;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.student.mapper.StudentMapper;
import com.example.demo.student.model.Student;
import com.example.demo.student.repository.StudentRepository;
import com.example.demo.course.model.Course;
import com.example.demo.course.repository.CourseRepository;
import com.example.demo.auth.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService Unit Tests")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;

    private UUID studentId;
    private UUID courseId;
    private Student student;
    private Course course;
    private StudentResponse studentResponse;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        courseId = UUID.randomUUID();

        course = Course.builder()
                .id(courseId)
                .name("Computer Science")
                .build();

        student = Student.builder()
                .id(studentId)
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@example.com")
                .password("dummyPassword")
                .role(Role.STUDENT)
                .course(course)
                .build();

        studentResponse = new StudentResponse(studentId, "John", "Smith", "john.smith@example.com", courseId, "Computer Science");
    }

    @Test
    @DisplayName("Should retrieve student by valid ID")
    void testGetStudent_Success() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(studentMapper.toResponse(student)).thenReturn(studentResponse);

        StudentResponse result = studentService.getStudent(studentId);

        assertNotNull(result);
        assertEquals("John", result.firstName());
        verify(studentRepository, times(1)).findById(studentId);
    }

    @Test
    @DisplayName("Should throw exception when student not found by ID")
    void testGetStudent_NotFound() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> studentService.getStudent(studentId));
    }

    @Test
    @DisplayName("Should retrieve all students successfully")
    void testGetAllStudents_Success() {
        List<Student> students = List.of(student);
        when(studentRepository.findAll()).thenReturn(students);
        when(studentMapper.toResponse(student)).thenReturn(studentResponse);

        List<StudentResponse> results = studentService.getAllStudents();

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve students by course ID")
    void testGetStudentsByCourse_Success() {
        List<Student> students = List.of(student);
        when(studentRepository.findByCourse(courseId)).thenReturn(students);
        when(studentMapper.toResponse(student)).thenReturn(studentResponse);

        List<StudentResponse> results = studentService.getStudentsByCourse(courseId);

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(studentRepository, times(1)).findByCourse(courseId);
    }

    @Test
    @DisplayName("Should update student with valid data")
    void testUpdateStudent_Success() {
        StudentRequest studentRequest = new StudentRequest("John", "Smith", "john.smith@example.com", courseId);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentMapper.toResponse(student)).thenReturn(studentResponse);

        StudentResponse result = studentService.updateStudent(studentId, studentRequest);

        assertNotNull(result);
        verify(studentRepository, times(1)).findById(studentId);
        verify(courseRepository, times(1)).findById(courseId);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent student")
    void testUpdateStudent_NotFound() {
        StudentRequest studentRequest = new StudentRequest("John", "Smith", "john.smith@example.com", courseId);

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> studentService.updateStudent(studentId, studentRequest));
    }

    @Test
    @DisplayName("Should delete student successfully")
    void testDeleteStudent_Success() {
        when(studentRepository.existsById(studentId)).thenReturn(true);

        studentService.deleteStudent(studentId);

        verify(studentRepository, times(1)).deleteById(studentId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent student")
    void testDeleteStudent_NotFound() {
        when(studentRepository.existsById(studentId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> studentService.deleteStudent(studentId));
    }
}
