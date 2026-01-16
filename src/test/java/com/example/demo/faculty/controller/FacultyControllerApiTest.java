package com.example.demo.faculty.controller;

import com.example.demo.faculty.dto.FacultyRequest;
import com.example.demo.faculty.dto.FacultyResponse;
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
@DisplayName("FacultyController API Tests")
class FacultyControllerApiTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacultyRepository facultyRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        facultyRepository.deleteAll();
        baseUrl = "/api/faculties";
    }

    @Test
    @DisplayName("Create Faculty")
    void testCreateFaculty() {
        FacultyRequest request = new FacultyRequest(
                "Engineering",
                "engineering@university.com",
                "+359888123456"
        );

        ResponseEntity<FacultyResponse> response = restTemplate.postForEntity(
                baseUrl, request, FacultyResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Engineering", response.getBody().name());
    }

    @Test
    @DisplayName("Get Faculty by ID")
    void testGetFacultyById() {
        var saved = facultyRepository.save(
                com.example.demo.faculty.model.Faculty.builder()
                        .name("Engineering")
                        .email("engineering@university.com")
                        .phone("+359888123456")
                        .build()
        );

        ResponseEntity<FacultyResponse> response = restTemplate.getForEntity(
                baseUrl + "/" + saved.getId(), FacultyResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Engineering", response.getBody().name());
    }

    @Test
    @DisplayName("Get All Faculties")
    void testGetAllFaculties() {
        facultyRepository.saveAll(List.of(
                com.example.demo.faculty.model.Faculty.builder()
                        .name("Engineering")
                        .email("engineering@university.com")
                        .phone("+359888123456")
                        .build(),
                com.example.demo.faculty.model.Faculty.builder()
                        .name("Science")
                        .email("science@university.com")
                        .phone("+359888654321")
                        .build()
        ));

        ResponseEntity<List<FacultyResponse>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<FacultyResponse>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().size() >= 2);
    }

    @Test
    @DisplayName("Update Faculty")
    void testUpdateFaculty() {
        var saved = facultyRepository.save(
                com.example.demo.faculty.model.Faculty.builder()
                        .name("Engineering")
                        .email("engineering@university.com")
                        .phone("+359888123456")
                        .build()
        );

        FacultyRequest updateRequest = new FacultyRequest(
                "Engineering Updated",
                "engupdated@university.com",
                "+359888999999"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FacultyRequest> entity = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<FacultyResponse> response = restTemplate.exchange(
                baseUrl + "/" + saved.getId(),
                HttpMethod.PUT,
                entity,
                FacultyResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Engineering Updated", response.getBody().name());
    }

    @Test
    @DisplayName("Delete Faculty")
    void testDeleteFaculty() {
        var saved = facultyRepository.save(
                com.example.demo.faculty.model.Faculty.builder()
                        .name("Engineering")
                        .email("engineering@university.com")
                        .phone("+359888123456")
                        .build()
        );

        restTemplate.delete(baseUrl + "/" + saved.getId());

        assertFalse(facultyRepository.findById(saved.getId()).isPresent());
    }
}
