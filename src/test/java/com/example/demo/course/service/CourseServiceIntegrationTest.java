package com.example.demo.course.service;

import com.example.demo.course.dto.CourseRequest;
import com.example.demo.course.model.Course;
import com.example.demo.course.repository.CourseRepository;
import com.example.demo.faculty.model.Faculty;
import com.example.demo.faculty.repository.FacultyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("CourseService Integration Tests")
class CourseServiceIntegrationTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    private Faculty faculty;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        facultyRepository.deleteAll();

        faculty = Faculty.builder()
                .name("Engineering")
                .email("engineering@university.com")
                .phone("+359888123456")
                .build();

        facultyRepository.save(faculty);
    }

    @Test
    @DisplayName("Should create course and persist in database")
    void testCreateCourse_Persistence() {
        CourseRequest request = new CourseRequest("Software Engineering", faculty.getId());

        var response = courseService.createCourse(request);

        assertNotNull(response.id());
        assertEquals("Software Engineering", response.name());
        assertTrue(courseRepository.findById(response.id()).isPresent());
    }

    @Test
    @DisplayName("Should retrieve course by ID from database")
    void testGetCourse_FromDatabase() {
        Course course = Course.builder()
                .name("Computer Systems Engineering")
                .faculty(faculty)
                .build();

        courseRepository.save(course);

        var response = courseService.getCourse(course.getId());

        assertNotNull(response);
        assertEquals("Computer Systems Engineering", response.name());
        assertEquals(faculty.getId(), response.facultyId());
    }

    @Test
    @DisplayName("Should retrieve courses by faculty from database")
    void testGetCoursesByFaculty_FromDatabase() {
        Course c1 = Course.builder()
                .name("Software Engineering")
                .faculty(faculty)
                .build();

        Course c2 = Course.builder()
                .name("Electrical Engineering")
                .faculty(faculty)
                .build();

        courseRepository.save(c1);
        courseRepository.save(c2);

        var responses = courseService.getCoursesByFaculty(faculty.getId());

        assertEquals(2, responses.size());
    }

    @Test
    @DisplayName("Should update course and persist changes")
    void testUpdateCourse_Persistence() {
        Course course = Course.builder()
                .name("Informatics")
                .faculty(faculty)
                .build();

        courseRepository.save(course);

        CourseRequest updateRequest =
                new CourseRequest("Applied Informatics", faculty.getId());

        var response = courseService.updateCourse(course.getId(), updateRequest);

        var updated = courseRepository.findById(course.getId()).orElseThrow();

        assertEquals("Applied Informatics", updated.getName());
        assertEquals("Applied Informatics", response.name());
    }

    @Test
    @DisplayName("Should delete course from database")
    void testDeleteCourse_Persistence() {
        Course course = Course.builder()
                .name("Industrial Engineering")
                .faculty(faculty)
                .build();

        courseRepository.save(course);
        UUID id = course.getId();

        courseService.deleteCourse(id);

        assertFalse(courseRepository.findById(id).isPresent());
    }
}
