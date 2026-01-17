package com.example.demo.attendance.repository;

import com.example.demo.attendance.model.Attendance;
import com.example.demo.attendance.model.AttendanceKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, AttendanceKey> {

    @Query("""
              SELECT COUNT(a) FROM Attendance a
              WHERE a.lesson.id = :lessonId
            """)
    int countByLessonId(UUID lessonId);

    List<Attendance> findByLessonId(UUID lessonId);
}
