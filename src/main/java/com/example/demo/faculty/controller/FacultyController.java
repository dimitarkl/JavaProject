package com.example.demo.faculty.controller;

import com.example.demo.faculty.dto.FacultyRequest;
import com.example.demo.faculty.dto.FacultyResponse;
import com.example.demo.faculty.service.FacultyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/faculties")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @Operation(summary = "Create a new faculty")
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<FacultyResponse> createFaculty(@RequestBody FacultyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(facultyService.createFaculty(request));
    }

    @Operation(summary = "Get a faculty by id")
    @GetMapping("/{id}")
    public ResponseEntity<FacultyResponse> getFaculty(@PathVariable UUID id) {
        return ResponseEntity.ok(facultyService.getFaculty(id));
    }

    @Operation(summary = "Get all faculties")
    @GetMapping
    public ResponseEntity<List<FacultyResponse>> getAllFaculties() {
        return ResponseEntity.ok(facultyService.getAllFaculties());
    }

    @Operation(summary = "Update a faculty by id")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<FacultyResponse> updateFaculty(
            @PathVariable UUID id,
            @RequestBody FacultyRequest request
    ) {
        return ResponseEntity.ok(facultyService.updateFaculty(id, request));
    }

    @Operation(summary = "Delete a faculty by id")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteFaculty(@PathVariable UUID id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.noContent().build();
    }
}
