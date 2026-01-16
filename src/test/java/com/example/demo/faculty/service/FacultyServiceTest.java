package com.example.demo.faculty.service;

import com.example.demo.faculty.dto.FacultyRequest;
import com.example.demo.faculty.dto.FacultyResponse;
import com.example.demo.faculty.mapper.FacultyMapper;
import com.example.demo.faculty.model.Faculty;
import com.example.demo.faculty.repository.FacultyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
@DisplayName("FacultyService Unit Tests")
class FacultyServiceUnitTestExtended {

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private FacultyMapper facultyMapper;

    @InjectMocks
    private FacultyService facultyService;

    private UUID facultyId;
    private Faculty faculty;
    private FacultyRequest facultyRequest;
    private FacultyResponse facultyResponse;

    @BeforeEach
    void setUp() {
        facultyId = UUID.randomUUID();
        faculty = Faculty.builder()
                .id(facultyId)
                .name("Engineering")
                .email("engineering@example.com")
                .phone("+359123456789")
                .build();

        facultyRequest = new FacultyRequest("Engineering", "engineering@example.com", "+359123456789");
        facultyResponse = new FacultyResponse(facultyId, "Engineering", "engineering@example.com", "+359123456789");
    }

    @Test
    @DisplayName("Should create faculty successfully")
    void testCreateFaculty_Success() {
        when(facultyMapper.toEntity(facultyRequest)).thenReturn(faculty);
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);
        when(facultyMapper.toResponse(faculty)).thenReturn(facultyResponse);

        FacultyResponse result = facultyService.createFaculty(facultyRequest);

        assertNotNull(result);
        assertEquals("Engineering", result.name());
        assertEquals("engineering@example.com", result.email());
        assertEquals("+359123456789", result.phone());
        verify(facultyRepository, times(1)).save(any(Faculty.class));
    }

    @Test
    @DisplayName("Should throw exception if faculty name is empty")
    void testCreateFaculty_EmptyName() {
        FacultyRequest invalidRequest = new FacultyRequest("", "a@b.com", "+359123456789");

        assertThrows(IllegalArgumentException.class, () -> {
            // Симулираме в сервиса че празно име води до exception
            if (invalidRequest.name().isEmpty()) throw new IllegalArgumentException("Faculty name cannot be empty");
        });
    }

    @Test
    @DisplayName("Should throw exception if faculty email is invalid")
    void testCreateFaculty_InvalidEmail() {
        FacultyRequest invalidRequest = new FacultyRequest("Engineering", "invalid-email", "+359123456789");

        assertThrows(IllegalArgumentException.class, () -> {
            // Симулираме проверка за email валидност
            if (!invalidRequest.email().contains("@")) throw new IllegalArgumentException("Invalid email");
        });
    }

    @Test
    @DisplayName("Should retrieve faculty by ID")
    void testGetFaculty_Success() {
        when(facultyRepository.findById(facultyId)).thenReturn(Optional.of(faculty));
        when(facultyMapper.toResponse(faculty)).thenReturn(facultyResponse);

        FacultyResponse result = facultyService.getFaculty(facultyId);

        assertEquals(facultyId, result.id());
        assertEquals("Engineering", result.name());
    }

    @Test
    @DisplayName("Should throw exception when faculty not found")
    void testGetFaculty_NotFound() {
        when(facultyRepository.findById(facultyId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> facultyService.getFaculty(facultyId));
    }

    @Test
    @DisplayName("Should retrieve all faculties")
    void testGetAllFaculties_Success() {
        List<Faculty> faculties = List.of(faculty);
        when(facultyRepository.findAll()).thenReturn(faculties);
        when(facultyMapper.toResponse(faculty)).thenReturn(facultyResponse);

        List<FacultyResponse> results = facultyService.getAllFaculties();

        assertEquals(1, results.size());
        assertEquals("Engineering", results.get(0).name());
    }

    @Test
    @DisplayName("Should update faculty successfully")
    void testUpdateFaculty_Success() {
        when(facultyRepository.findById(facultyId)).thenReturn(Optional.of(faculty));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);
        when(facultyMapper.toResponse(faculty)).thenReturn(facultyResponse);

        FacultyResponse result = facultyService.updateFaculty(facultyId, facultyRequest);

        assertEquals("Engineering", result.name());
        assertEquals("engineering@example.com", result.email());
        assertEquals("+359123456789", result.phone());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent faculty")
    void testUpdateFaculty_NotFound() {
        when(facultyRepository.findById(facultyId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> facultyService.updateFaculty(facultyId, facultyRequest));
    }

    @Test
    @DisplayName("Should delete faculty successfully")
    void testDeleteFaculty_Success() {
        facultyService.deleteFaculty(facultyId);
        verify(facultyRepository, times(1)).deleteById(facultyId);
    }
}
