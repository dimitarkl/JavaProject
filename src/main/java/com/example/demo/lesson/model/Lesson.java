package com.example.demo.lesson.model;

import com.example.demo.subject.model.Subject;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subjects_id", nullable = false)
    private Subject subject;
}