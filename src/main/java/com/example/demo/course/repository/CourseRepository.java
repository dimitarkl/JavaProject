package com.example.demo.course.repository;

import com.example.demo.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    @Query("""
        SELECT c FROM Course c
        WHERE c.faculty.id = :facultyId
    """)
    List<Course> findByFaculty(@Param("facultyId") UUID facultyId);
}
