package com.example.demo.student.service;

import com.example.demo.auth.dto.RegisterStudentRequest;
import com.example.demo.auth.service.AuthService;
import com.example.demo.student.dto.StudentRequest;
import com.example.demo.student.dto.StudentResponse;
import com.example.demo.student.mapper.StudentMapper;
import com.example.demo.student.model.Student;
import com.example.demo.student.repository.StudentRepository;
import com.example.demo.course.model.Course;
import com.example.demo.course.repository.CourseRepository;
import com.example.demo.faculty.model.Faculty;
import com.example.demo.faculty.repository.FacultyRepository;
import com.example.demo.auth.Role;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
@DisplayName("Student Integration Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    private Faculty faculty;
    private Course course;
    private UUID courseId;

    @BeforeAll
    void initData() {
        facultyRepository.deleteAll();
        courseRepository.deleteAll();
        studentRepository.deleteAll();

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

    @BeforeEach
    void cleanStudents() {
        studentRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create and persist student in database via registration")
    void testRegisterStudent_Persistence() {
        RegisterStudentRequest request = new RegisterStudentRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@university.com");
        request.setPassword("password123");
        request.setCourseId(courseId);

        authService.registerStudent(request);

        studentRepository.flush();

        List<Student> allStudents = studentRepository.findAll();
        assertFalse(allStudents.isEmpty(), "No students found in DB");

        Student student = allStudents.get(0);
        StudentResponse response = studentMapper.toResponse(student);

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
                .password("test123")
                .role(Role.STUDENT)
                .course(course)
                .build();
        studentRepository.save(student);
        studentRepository.flush();

        StudentResponse response = studentService.getStudent(student.getId());

        assertNotNull(response);
        assertEquals("Jane", response.firstName());
        assertEquals("jane.smith@university.com", response.email());
        assertTrue(studentRepository.findById(student.getId()).isPresent());
    }

    @Test
    @DisplayName("Should update student and persist changes")
    void testUpdateStudent_Persistence() {
        Student student = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@university.com")
                .password("initial")
                .role(Role.STUDENT)
                .course(course)
                .build();
        studentRepository.save(student);
        studentRepository.flush();

        StudentRequest updateRequest = new StudentRequest(
                "Maria",
                "Ivanova",
                "m.ivanova@university.com",
                courseId
        );

        StudentResponse response = studentService.updateStudent(student.getId(), updateRequest);

        assertEquals("Maria", response.firstName());

        Student updated = studentRepository.findById(student.getId()).orElseThrow();
        assertEquals("Maria", updated.getFirstName());
        assertEquals("m.ivanova@university.com", updated.getEmail());
        assertTrue(studentRepository.findById(student.getId()).isPresent());
    }

    @Test
    @DisplayName("Should retrieve all students from database")
    void testGetAllStudents_FromDatabase() {
        Student s1 = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@university.com")
                .password("pwd1")
                .role(Role.STUDENT)
                .course(course)
                .build();

        Student s2 = Student.builder()
                .firstName("Anna")
                .lastName("Petrova")
                .email("anna.petrova@university.com")
                .password("pwd2")
                .role(Role.STUDENT)
                .course(course)
                .build();

        studentRepository.saveAll(List.of(s1, s2));
        studentRepository.flush();

        List<StudentResponse> responses = studentService.getAllStudents();

        assertEquals(2, responses.size());
        assertTrue(studentRepository.findById(s1.getId()).isPresent());
        assertTrue(studentRepository.findById(s2.getId()).isPresent());
    }

    @Test
    @DisplayName("Should retrieve students by course from database")
    void testGetStudentsByCourse_FromDatabase() {
        Student s1 = Student.builder()
                .firstName("Georgi")
                .lastName("Nikolov")
                .email("georgi.nikolov@university.com")
                .password("pwd123")
                .role(Role.STUDENT)
                .course(course)
                .build();

        studentRepository.save(s1);
        studentRepository.flush();

        List<StudentResponse> responses = studentService.getStudentsByCourse(courseId);

        assertEquals(1, responses.size());
        assertEquals("Georgi", responses.get(0).firstName());
        assertTrue(studentRepository.findById(s1.getId()).isPresent());
    }

    @Test
    @DisplayName("Should delete student from database")
    void testDeleteStudent_Persistence() {
        Student student = Student.builder()
                .firstName("Ivan")
                .lastName("Dimitrov")
                .email("ivan.dimitrov@university.com")
                .password("pwd456")
                .role(Role.STUDENT)
                .course(course)
                .build();

        studentRepository.save(student);
        studentRepository.flush();

        UUID studentId = student.getId();
        studentService.deleteStudent(studentId);

        assertTrue(studentRepository.findById(studentId).isEmpty());
    }
}
