package com.example.demo.lesson.repository;

import com.example.demo.lesson.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    @Query("SELECT l FROM Lesson l WHERE l.startDate > :now ORDER BY l.startDate ASC")
    List<Lesson> findAllFutureLessons(@Param("now") LocalDateTime now);

}