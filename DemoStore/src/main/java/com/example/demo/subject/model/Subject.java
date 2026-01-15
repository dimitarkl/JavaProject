package com.example.demo.subject.model;

import com.example.demo.lesson.model.Lesson;
import com.example.demo.teacher.model.Teacher;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "subject")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "max_attendance", nullable = false)
    private Integer maxAttendance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    private List<Lesson> lessons;
}