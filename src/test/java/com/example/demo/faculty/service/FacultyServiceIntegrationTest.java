package com.example.demo.faculty.service;

import com.example.demo.faculty.dto.FacultyRequest;
import com.example.demo.faculty.model.Faculty;
import com.example.demo.faculty.repository.FacultyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("FacultyService Integration Tests")
class FacultyServiceIntegrationTest {

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private FacultyRepository facultyRepository;

    @BeforeEach
    void setUp() {
        facultyRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create faculty and persist in database")
    void testCreateFaculty_Persistence() {
        FacultyRequest request = new FacultyRequest(
                "Engineering",
                "engineering@university.com",
                "+359888123456"
        );

        var response = facultyService.createFaculty(request);

        assertNotNull(response.id());

        var saved = facultyRepository.findById(response.id());
        assertTrue(saved.isPresent());
        assertEquals("Engineering", saved.get().getName());
        assertEquals("engineering@university.com", saved.get().getEmail());
        assertEquals("+359888123456", saved.get().getPhone());
    }

    @Test
    @DisplayName("Should retrieve all faculties from database")
    void testGetAllFaculties_FromDatabase() {
        Faculty f1 = Faculty.builder()
                .name("Engineering")
                .email("engineering@university.com")
                .phone("+359888111111")
                .build();

        Faculty f2 = Faculty.builder()
                .name("Science")
                .email("science@university.com")
                .phone("+359888222222")
                .build();

        facultyRepository.saveAll(java.util.List.of(f1, f2));

        var responses = facultyService.getAllFaculties();

        assertTrue(responses.size() >= 2);
    }

    @Test
    @DisplayName("Should retrieve faculty by id")
    void testGetFaculty_ById() {
        Faculty faculty = Faculty.builder()
                .name("Engineering")
                .email("engineering@university.com")
                .phone("+359888123456")
                .build();

        var saved = facultyRepository.save(faculty);

        var response = facultyService.getFaculty(saved.getId());

        assertEquals("Engineering", response.name());
        assertEquals("engineering@university.com", response.email());
        assertEquals("+359888123456", response.phone());
    }

    @Test
    @DisplayName("Should update faculty successfully")
    void testUpdateFaculty() {
        Faculty faculty = Faculty.builder()
                .name("Engineering")
                .email("old@university.com")
                .phone("+359888000000")
                .build();

        var saved = facultyRepository.save(faculty);

        FacultyRequest updateRequest = new FacultyRequest(
                "Engineering and Technology",
                "new@university.com",
                "+359888999999"
        );

        var response = facultyService.updateFaculty(saved.getId(), updateRequest);

        assertEquals("Engineering and Technology", response.name());
        assertEquals("new@university.com", response.email());
        assertEquals("+359888999999", response.phone());
    }

    @Test
    @DisplayName("Should delete faculty from database")
    void testDeleteFaculty() {
        Faculty faculty = Faculty.builder()
                .name("Engineering")
                .email("engineering@university.com")
                .phone("+359888123456")
                .build();

        var saved = facultyRepository.save(faculty);

        facultyService.deleteFaculty(saved.getId());

        assertFalse(facultyRepository.findById(saved.getId()).isPresent());
    }
}
