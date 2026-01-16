package com.example.demo.course.service;

import com.example.demo.course.dto.CourseRequest;
import com.example.demo.course.dto.CourseResponse;
import com.example.demo.course.mapper.CourseMapper;
import com.example.demo.course.model.Course;
import com.example.demo.course.repository.CourseRepository;
import com.example.demo.faculty.model.Faculty;
import com.example.demo.faculty.repository.FacultyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("CourseService Unit Tests")
class CourseServiceUnitTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseService courseService;

    private UUID courseId;
    private UUID facultyId;
    private Course course;
    private Faculty faculty;
    private CourseRequest courseRequest;
    private CourseResponse courseResponse;

    @BeforeEach
    void setUp() {
        courseId = UUID.randomUUID();
        facultyId = UUID.randomUUID();

        faculty = Faculty.builder()
                .id(facultyId)
                .name("Engineering")
                .build();

        course = Course.builder()
                .id(courseId)
                .name("Computer Engineering")
                .faculty(faculty)
                .build();

        courseRequest = new CourseRequest("Computer Engineering", facultyId);
        courseResponse = new CourseResponse(courseId, "Computer Engineering", facultyId, "Engineering");
    }

    @Test
    @DisplayName("Should create course successfully")
    void testCreateCourse_Success() {
        when(facultyRepository.findById(facultyId)).thenReturn(Optional.of(faculty));
        when(courseMapper.toEntity(courseRequest)).thenReturn(course);
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(courseMapper.toResponse(course)).thenReturn(courseResponse);

        CourseResponse result = courseService.createCourse(courseRequest);

        assertNotNull(result);
        assertEquals("Computer Engineering", result.name());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("Should throw exception when faculty not found during course creation")
    void testCreateCourse_FacultyNotFound() {
        when(facultyRepository.findById(facultyId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> courseService.createCourse(courseRequest));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Should retrieve course by ID")
    void testGetCourse_Success() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseMapper.toResponse(course)).thenReturn(courseResponse);

        CourseResponse result = courseService.getCourse(courseId);

        assertNotNull(result);
        assertEquals("Computer Engineering", result.name());
    }

    @Test
    @DisplayName("Should throw exception when course not found by ID")
    void testGetCourse_NotFound() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> courseService.getCourse(courseId));
    }

    @Test
    @DisplayName("Should retrieve all courses by faculty")
    void testGetCoursesByFaculty_Success() {
        List<Course> courses = List.of(course);
        when(courseRepository.findByFaculty(facultyId)).thenReturn(courses);
        when(courseMapper.toResponse(course)).thenReturn(courseResponse);

        List<CourseResponse> results = courseService.getCoursesByFaculty(facultyId);

        assertEquals(1, results.size());
        assertEquals("Computer Engineering", results.get(0).name());
        verify(courseRepository, times(1)).findByFaculty(facultyId);
    }

    @Test
    @DisplayName("Should update course successfully")
    void testUpdateCourse_Success() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(courseMapper.toResponse(course)).thenReturn(courseResponse);

        CourseResponse result = courseService.updateCourse(courseId, courseRequest);

        assertNotNull(result);
        assertEquals("Computer Engineering", result.name());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent course")
    void testUpdateCourse_NotFound() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> courseService.updateCourse(courseId, courseRequest));
    }

    @Test
    @DisplayName("Should delete course successfully")
    void testDeleteCourse_Success() {
        when(courseRepository.existsById(courseId)).thenReturn(true);

        courseService.deleteCourse(courseId);

        verify(courseRepository, times(1)).deleteById(courseId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent course")
    void testDeleteCourse_NotFound() {
        when(courseRepository.existsById(courseId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> courseService.deleteCourse(courseId));
    }
}
