package com.example.demo.attendance.model;

import com.example.demo.lesson.model.Lesson;
import com.example.demo.student.model.Student;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "attendance", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"lesson_id", "student_id"}) // Ensures a student can't attend the same lesson twice
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @EmbeddedId
    private AttendanceKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lessonId")
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;
}
