package com.example.demo.course.service;

import com.example.demo.course.dto.CourseRequest;
import com.example.demo.course.dto.CourseResponse;
import com.example.demo.course.mapper.CourseMapper;
import com.example.demo.course.model.Course;
import com.example.demo.course.repository.CourseRepository;
import com.example.demo.faculty.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;
    private final CourseMapper mapper;

    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        var faculty = facultyRepository.findById(request.facultyId())
                .orElseThrow(() -> new IllegalArgumentException("Faculty not found"));

        var course = mapper.toEntity(request);
        course.setFaculty(faculty);

        var saved = courseRepository.save(course);
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourse(UUID id) {
        var course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        return mapper.toResponse(course);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByFaculty(UUID facultyId) {
        return courseRepository.findByFaculty(facultyId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public CourseResponse updateCourse(UUID id, CourseRequest request) {
        var course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        course.setName(request.name());
        var saved = courseRepository.save(course);
        return mapper.toResponse(saved);
    }

    @Transactional
    public void deleteCourse(UUID id) {
        if (!courseRepository.existsById(id)) {
            throw new IllegalArgumentException("Course not found");
        }
        courseRepository.deleteById(id);
    }
}
