package com.example.demo.faculty.repository;

import com.example.demo.faculty.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, UUID> {
    Optional<Faculty> findByEmail(String email);
    Optional<Faculty> findByPhone(String phone);
}
