package com.example.demo.course.controller;

import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.RegisterStudentRequest;
import com.example.demo.course.dto.CourseRequest;
import com.example.demo.course.dto.CourseResponse;
import com.example.demo.course.model.Course;
import com.example.demo.course.repository.CourseRepository;
import com.example.demo.faculty.model.Faculty;
import com.example.demo.faculty.repository.FacultyRepository;
import com.example.demo.student.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("CourseController API Tests")
class CourseControllerApiTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRepository studentRepository;

    private String baseUrl;
    private Faculty faculty;
    private String token;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        courseRepository.deleteAll();
        facultyRepository.deleteAll();

        baseUrl = "/api/courses";

        faculty = facultyRepository.save(
                Faculty.builder()
                        .name("Engineering")
                        .email("engineering@university.com")
                        .phone("+359888123456")
                        .build()
        );

        token = registerAndLoginStudent();
    }

    private String registerAndLoginStudent() {

        Course tempCourse = courseRepository.save(
                Course.builder().name("Temp course").faculty(faculty).build()
        );

        RegisterStudentRequest register = new RegisterStudentRequest();
        register.setEmail("student1@uni.com");
        register.setPassword("password123");
        register.setFirstName("Ivan");
        register.setLastName("Ivanov");
        register.setCourseId(tempCourse.getId());


        String registerUrl = restTemplate.getRootUri() + "/api/auth/register/student";
        restTemplate.postForEntity(registerUrl, register, String.class);


        LoginRequest login = new LoginRequest();
        login.setEmail("student1@uni.com");
        login.setPassword("password123");

        String loginUrl = restTemplate.getRootUri() + "/api/auth/login";
        ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, login, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = mapper.readValue(response.getBody(), Map.class);
            return map.get("accessToken");
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse login response JSON", e);
        }
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    @DisplayName("Create Course")
    void testCreateCourse() {
        CourseRequest request = new CourseRequest("Computer Science", faculty.getId());

        ResponseEntity<CourseResponse> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(request, authHeaders()),
                CourseResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Computer Science", response.getBody().name());
    }

    @Test
    @DisplayName("Get Course by ID")
    void testGetCourseById() {
        Course saved = courseRepository.save(
                Course.builder().name("Computer Science").faculty(faculty).build()
        );

        ResponseEntity<CourseResponse> response = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                CourseResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Computer Science", response.getBody().name());
    }

    @Test
    @DisplayName("Get Courses by Faculty")
    void testGetCoursesByFaculty() {
        courseRepository.saveAll(List.of(
                Course.builder().name("Computer Science").faculty(faculty).build(),
                Course.builder().name("Electrical Engineering").faculty(faculty).build()
        ));

        ResponseEntity<List<CourseResponse>> response = restTemplate.exchange(
                baseUrl + "/faculty/" + faculty.getId(),
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().size() >= 2);
    }

    @Test
    @DisplayName("Update Course")
    void testUpdateCourse() {
        Course saved = courseRepository.save(
                Course.builder().name("Computer Science").faculty(faculty).build()
        );

        CourseRequest updateRequest = new CourseRequest("Computer Engineering", faculty.getId());

        ResponseEntity<CourseResponse> response = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest, authHeaders()),
                CourseResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Computer Engineering", response.getBody().name());
    }

    @Test
    @DisplayName("Delete Course")
    void testDeleteCourse() {
        Course saved = courseRepository.save(
                Course.builder().name("Computer Science").faculty(faculty).build()
        );

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(authHeaders()),
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(courseRepository.findById(saved.getId()).isEmpty());
    }
}
