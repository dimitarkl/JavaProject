package com.example.demo.course.controller;

import com.example.demo.course.dto.CourseRequest;
import com.example.demo.course.dto.CourseResponse;
import com.example.demo.course.model.Course;
import com.example.demo.course.repository.CourseRepository;
import com.example.demo.faculty.model.Faculty;
import com.example.demo.faculty.repository.FacultyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("CourseController API Tests")
class CourseControllerApiTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    private String baseUrl;
    private Faculty faculty;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        facultyRepository.deleteAll();
        baseUrl = "/api/courses";

        faculty = Faculty.builder()
                .name("Engineering")
                .email("engineering@university.com")
                .phone("+359888123456")
                .build();
        facultyRepository.save(faculty);
    }

    @Test
    @DisplayName("Create Course")
    void testCreateCourse() {
        CourseRequest request = new CourseRequest("Computer Science", faculty.getId());

        ResponseEntity<CourseResponse> response = restTemplate.postForEntity(
                baseUrl, request, CourseResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Computer Science", response.getBody().name());
        assertEquals(faculty.getId(), response.getBody().facultyId());
    }

    @Test
    @DisplayName("Get Course by ID")
    void testGetCourseById() {
        Course saved = courseRepository.save(
                Course.builder()
                        .name("Computer Science")
                        .faculty(faculty)
                        .build()
        );

        ResponseEntity<CourseResponse> response = restTemplate.getForEntity(
                baseUrl + "/" + saved.getId(), CourseResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
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
                null,
                new ParameterizedTypeReference<List<CourseResponse>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().size() >= 2);
    }

    @Test
    @DisplayName("Update Course")
    void testUpdateCourse() {
        Course saved = courseRepository.save(
                Course.builder()
                        .name("Computer Science")
                        .faculty(faculty)
                        .build()
        );

        CourseRequest updateRequest = new CourseRequest("Computer Engineering", faculty.getId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CourseRequest> entity = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<CourseResponse> response = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.PUT,
                entity,
                CourseResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Computer Engineering", response.getBody().name());
    }

    @Test
    @DisplayName("Delete Course")
    void testDeleteCourse() {
        Course saved = courseRepository.save(
                Course.builder()
                        .name("Computer Science")
                        .faculty(faculty)
                        .build()
        );

        restTemplate.delete(baseUrl + "/" + saved.getId());

        assertFalse(courseRepository.findById(saved.getId()).isPresent());
    }
}
