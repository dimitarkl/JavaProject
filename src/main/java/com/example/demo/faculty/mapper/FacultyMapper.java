package com.example.demo.faculty.mapper;

import com.example.demo.faculty.dto.FacultyRequest;
import com.example.demo.faculty.dto.FacultyResponse;
import com.example.demo.faculty.model.Faculty;
import org.springframework.stereotype.Component;

@Component
public class FacultyMapper {

    public Faculty toEntity(FacultyRequest dto) {
        return Faculty.builder()
                .name(dto.name())
                .email(dto.email())
                .phone(dto.phone())
                .build();
    }

    public FacultyResponse toResponse(Faculty entity) {
        return new FacultyResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone()
        );
    }
}
