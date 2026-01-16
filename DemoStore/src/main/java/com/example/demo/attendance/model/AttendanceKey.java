package com.example.demo.attendance.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AttendanceKey implements Serializable {

    @Column(name = "lesson_id")
    private UUID lessonId;

    @Column(name = "student_id")
    private UUID studentId;
}