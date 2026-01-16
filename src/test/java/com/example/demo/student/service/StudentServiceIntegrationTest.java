package com.example.demo.student.service;

import com.example.demo.student.dto.StudentRequest;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.student.model.Student;
import com.example.demo.student.repository.StudentRepository;
import com.example.demo.course.model.Course;
import com.example.demo.course.repository.CourseRepository;
import com.example.demo.faculty.model.Faculty;
import com.example.demo.faculty.repository.FacultyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("StudentService Integration Tests")
class StudentServiceIntegrationTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    private Faculty faculty;
    private Course course;
    private UUID courseId;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        courseRepository.deleteAll();
        facultyRepository.deleteAll();

        faculty = Faculty.builder()
                .name("Engineering")
                .email("engineering@university.com")
                .phone("+359888123456")
                .build();

        facultyRepository.save(faculty);

        course = Course.builder()
                .name("Software Engineering")
                .faculty(faculty)
                .build();

        courseRepository.save(course);
        courseId = course.getId();
    }

    @Test
    @DisplayName("Should create and persist student in database")
    void testCreateStudent_Persistence() {
        StudentRequest request =
                new StudentRequest("John", "Doe", "john.doe@university.com", courseId);

        StudentResponse response = studentService.createStudent(request);

        assertNotNull(response.id());
        assertEquals("John", response.firstName());
        assertTrue(studentRepository.findById(response.id()).isPresent());
    }

    @Test
    @DisplayName("Should retrieve student from database successfully")
    void testGetStudent_FromDatabase() {
        Student student = Student.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@university.com")
                .course(course)
                .build();

        studentRepository.save(student);

        StudentResponse response = studentService.getStudent(student.getId());

        assertNotNull(response);
        assertEquals("Jane", response.firstName());
        assertEquals("jane.smith@university.com", response.email());
    }

    @Test
    @DisplayName("Should update student and persist changes")
    void testUpdateStudent_Persistence() {
        Student student = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@university.com")
                .course(course)
                .build();

        studentRepository.save(student);

        StudentRequest updateRequest =
                new StudentRequest("Maria", "Ivanova", "m.ivanova@university.com", courseId);

        StudentResponse response =
                studentService.updateStudent(student.getId(), updateRequest);

        assertEquals("Maria", response.firstName());

        Student updated = studentRepository.findById(student.getId()).orElseThrow();
        assertEquals("Maria", updated.getFirstName());
        assertEquals("m.ivanova@university.com", updated.getEmail());
    }

    @Test
    @DisplayName("Should retrieve all students from database")
    void testGetAllStudents_FromDatabase() {
        Student s1 = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@university.com")
                .course(course)
                .build();

        Student s2 = Student.builder()
                .firstName("Anna")
                .lastName("Petrova")
                .email("anna.petrova@university.com")
                .course(course)
                .build();

        studentRepository.saveAll(List.of(s1, s2));

        List<StudentResponse> responses = studentService.getAllStudents();

        assertEquals(2, responses.size());
    }

    @Test
    @DisplayName("Should retrieve students by course from database")
    void testGetStudentsByCourse_FromDatabase() {
        Student s1 = Student.builder()
                .firstName("Georgi")
                .lastName("Nikolov")
                .email("georgi.nikolov@university.com")
                .course(course)
                .build();

        studentRepository.save(s1);

        List<StudentResponse> responses =
                studentService.getStudentsByCourse(courseId);

        assertEquals(1, responses.size());
        assertEquals("Georgi", responses.get(0).firstName());
    }

    @Test
    @DisplayName("Should delete student from database")
    void testDeleteStudent_Persistence() {
        Student student = Student.builder()
                .firstName("Ivan")
                .lastName("Dimitrov")
                .email("ivan.dimitrov@university.com")
                .course(course)
                .build();

        studentRepository.save(student);
        UUID studentId = student.getId();

        studentService.deleteStudent(studentId);

        assertTrue(studentRepository.findById(studentId).isEmpty());
    }
}
