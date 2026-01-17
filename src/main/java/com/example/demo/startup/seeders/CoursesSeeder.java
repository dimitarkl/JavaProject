package com.example.demo.startup.seeders;

import com.example.demo.course.model.Course;
import com.example.demo.course.repository.CourseRepository;
import com.example.demo.faculty.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Profile("dev")
@Order(1)
@Component
@RequiredArgsConstructor
public class CoursesSeeder implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;

    @Override
    public void run(String... args) throws Exception {
        Course course = Course.builder()
                .name("Course 1")
                .faculty(facultyRepository.findByPhone("123456789").orElseThrow())
                .build();
        if(courseRepository.findAll().size() == 0)
        courseRepository.save(course);
        System.out.println("--- 1 Course were seeded! ---");
    }
}
