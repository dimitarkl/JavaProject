package com.example.demo.course.mapper;

import com.example.demo.course.dto.CourseRequest;
import com.example.demo.course.dto.CourseResponse;
import com.example.demo.course.model.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    public Course toEntity(CourseRequest dto) {
        return Course.builder()
                .name(dto.name())
                .build();
    }

    public CourseResponse toResponse(Course entity) {
        return new CourseResponse(
                entity.getId(),
                entity.getName(),
                entity.getFaculty().getId(),
                entity.getFaculty().getName()
        );
    }
}