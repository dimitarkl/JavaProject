package com.example.demo.faculty.service;

import com.example.demo.faculty.dto.FacultyRequest;
import com.example.demo.faculty.dto.FacultyResponse;
import com.example.demo.faculty.mapper.FacultyMapper;
import com.example.demo.faculty.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final FacultyMapper mapper;

    @Transactional
    public FacultyResponse createFaculty(FacultyRequest request) {
        var faculty = mapper.toEntity(request);
        var saved = facultyRepository.save(faculty);
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public FacultyResponse getFaculty(UUID id) {
        var faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Faculty not found"));
        return mapper.toResponse(faculty);
    }

    @Transactional(readOnly = true)
    public List<FacultyResponse> getAllFaculties() {
        return facultyRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public FacultyResponse updateFaculty(UUID id, FacultyRequest request) {
        var faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Faculty not found"));

        faculty.setName(request.name());
        faculty.setEmail(request.email());
        faculty.setPhone(request.phone());

        var saved = facultyRepository.save(faculty);
        return mapper.toResponse(saved);
    }

    @Transactional
    public void deleteFaculty(UUID id) {
        facultyRepository.deleteById(id);
    }
}
