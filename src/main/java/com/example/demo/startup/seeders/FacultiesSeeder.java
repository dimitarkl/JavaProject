package com.example.demo.startup.seeders;

import com.example.demo.faculty.model.Faculty;
import com.example.demo.faculty.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Profile("dev")
@Order(0)
@Component
@RequiredArgsConstructor
public class FacultiesSeeder implements CommandLineRunner {
    private final FacultyRepository facultyRepository;

    @Override
    public void run(String... args) throws Exception {

        Faculty faculty = Faculty.builder()
                .name("Faculty 1")
                .phone("123456789")
                .email("a@c.com")
                .build();

        facultyRepository.save(faculty);

        System.out.println("--- 1 faculty seeded! ---");

    }
}
