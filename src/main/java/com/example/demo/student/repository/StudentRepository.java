package com.example.demo.student.repository;

import com.example.demo.student.model.Student;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

    @Query("""
        SELECT s FROM Student s
        WHERE s.course.id = :courseId
    """)
    List<Student> findByCourse(@Param("courseId") UUID courseId);
}
