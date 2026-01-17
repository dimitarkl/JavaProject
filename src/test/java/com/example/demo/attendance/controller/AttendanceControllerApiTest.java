package com.example.demo.attendance.controller;

import com.example.demo.attendance.dtos.AttendanceResponse;
import com.example.demo.attendance.dtos.AttendancesByLessonResponse;
import com.example.demo.attendance.repository.AttendanceRepository;
import com.example.demo.auth.dto.RegisterStudentRequest;
import com.example.demo.auth.dto.RegisterTeacherRequest;
import com.example.demo.course.model.Course;
import com.example.demo.course.repository.CourseRepository;
import com.example.demo.faculty.model.Faculty;
import com.example.demo.faculty.repository.FacultyRepository;
import com.example.demo.lesson.model.Lesson;
import com.example.demo.lesson.repository.LessonRepository;
import com.example.demo.student.repository.StudentRepository;
import com.example.demo.subject.model.Subject;
import com.example.demo.subject.repository.SubjectRepository;
import com.example.demo.teacher.model.Teacher;
import com.example.demo.teacher.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Attendance API Integration Tests")
class AttendanceControllerApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    private String baseUrl;
    private String authRegisterStudentUrl;
    private String authRegisterTeacherUrl;
    private Faculty faculty;
    private Course course;
    private Subject subject;
    private Lesson lesson;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        attendanceRepository.deleteAll();
        lessonRepository.deleteAll();
        subjectRepository.deleteAll();
        studentRepository.deleteAll();
        teacherRepository.deleteAll();
        courseRepository.deleteAll();
        facultyRepository.deleteAll();

        faculty = Faculty.builder()
                .name("Engineering")
                .email("engineering@example.com")
                .phone("123456789")
                .build();
        facultyRepository.save(faculty);

        course = Course.builder()
                .name("Computer Science")
                .faculty(faculty)
                .build();
        courseRepository.save(course);

        baseUrl = "http://localhost:" + port + "/api/attendances";
        authRegisterStudentUrl = "http://localhost:" + port + "/api/auth/register/student";
        authRegisterTeacherUrl = "http://localhost:" + port + "/api/auth/register/teacher";
    }

    private String registerAndLoginStudent(String email, String password) {
        RegisterStudentRequest registerRequest = new RegisterStudentRequest();
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setCourseId(course.getId());

        ResponseEntity<String> registerResponse =
                restTemplate.postForEntity(authRegisterStudentUrl, registerRequest, String.class);

        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());
        assertNotNull(registerResponse.getBody());

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, String> map = mapper.readValue(registerResponse.getBody(), java.util.Map.class);
            String token = map.get("accessToken");
            assertNotNull(token);
            return token;
        } catch (Exception e) {
            fail("Failed to parse AuthResponse JSON: " + e.getMessage());
            return null;
        }
    }

    private String registerAndLoginTeacher(String email, String password) {
        RegisterTeacherRequest registerRequest = new RegisterTeacherRequest();
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        registerRequest.setFirstName("Jane");
        registerRequest.setLastName("Smith");

        ResponseEntity<String> registerResponse =
                restTemplate.postForEntity(authRegisterTeacherUrl, registerRequest, String.class);

        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());
        assertNotNull(registerResponse.getBody());

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, String> map = mapper.readValue(registerResponse.getBody(), java.util.Map.class);
            String token = map.get("accessToken");
            assertNotNull(token);
            return token;
        } catch (Exception e) {
            fail("Failed to parse AuthResponse JSON: " + e.getMessage());
            return null;
        }
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private void createLesson() {
        teacher = teacherRepository.findAll().get(0);

        subject = Subject.builder()
                .name("Mathematics")
                .teacher(teacher)
                .build();
        subjectRepository.save(subject);

        lesson = Lesson.builder()
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(1).plusHours(2))
                .type("Lecture")
                .subject(subject)
                .build();
        lessonRepository.save(lesson);
    }

    @Test
    @DisplayName("Should verify attendance successfully as student")
    void testVerifyAttendance_Success() {
        registerAndLoginTeacher("teacher@test.com", "123456");
        createLesson();
        String studentToken = registerAndLoginStudent("student@test.com", "123456");

        HttpEntity<Void> entity = new HttpEntity<>(authHeaders(studentToken));

        ResponseEntity<AttendanceResponse> response =
                restTemplate.exchange(baseUrl + "/" + lesson.getId() + "/verify",
                        HttpMethod.POST, entity, AttendanceResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(lesson.getId(), response.getBody().lessonId());
        assertEquals("John", response.getBody().studentFirstName());
        assertEquals("Doe", response.getBody().studentLastName());
    }

    @Test
    @DisplayName("Should get attendances by lesson ID as teacher")
    void testGetAttendancesByLessonId_Success() {
        String teacherToken = registerAndLoginTeacher("teacher3@test.com", "123456");
        createLesson();
        String studentToken = registerAndLoginStudent("student3@test.com", "123456");

        HttpEntity<Void> studentEntity = new HttpEntity<>(authHeaders(studentToken));
        restTemplate.exchange(baseUrl + "/" + lesson.getId() + "/verify",
                HttpMethod.POST, studentEntity, AttendanceResponse.class);

        HttpEntity<Void> teacherEntity = new HttpEntity<>(authHeaders(teacherToken));
        ResponseEntity<AttendancesByLessonResponse> response =
                restTemplate.exchange(baseUrl + "/" + lesson.getId(),
                        HttpMethod.GET, teacherEntity, AttendancesByLessonResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalAttendances());
        assertEquals(1, response.getBody().getAttendances().size());
        assertEquals(lesson.getId(), response.getBody().getAttendances().get(0).lessonId());
    }

    @Test
    @DisplayName("Should return empty list when no attendances for lesson")
    void testGetAttendancesByLessonId_NoAttendances() {
        String teacherToken = registerAndLoginTeacher("teacher4@test.com", "123456");
        createLesson();

        HttpEntity<Void> entity = new HttpEntity<>(authHeaders(teacherToken));
        ResponseEntity<AttendancesByLessonResponse> response =
                restTemplate.exchange(baseUrl + "/" + lesson.getId(),
                        HttpMethod.GET, entity, AttendancesByLessonResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getTotalAttendances());
        assertEquals(0, response.getBody().getAttendances().size());
    }

    @Test
    @DisplayName("Should fail when student tries to get attendances")
    void testGetAttendancesByLessonId_Forbidden() {
        registerAndLoginTeacher("teacher5@test.com", "123456");
        createLesson();
        String studentToken = registerAndLoginStudent("student5@test.com", "123456");

        HttpEntity<Void> entity = new HttpEntity<>(authHeaders(studentToken));
        ResponseEntity<String> response =
                restTemplate.exchange(baseUrl + "/" + lesson.getId(),
                        HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should fail when teacher tries to verify attendance")
    void testVerifyAttendance_ForbiddenForTeacher() {
        String teacherToken = registerAndLoginTeacher("teacher6@test.com", "123456");
        createLesson();

        HttpEntity<Void> entity = new HttpEntity<>(authHeaders(teacherToken));
        ResponseEntity<String> response =
                restTemplate.exchange(baseUrl + "/" + lesson.getId() + "/verify",
                        HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}

