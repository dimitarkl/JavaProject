package com.example.demo.student.model;
import com.example.demo.course.model.Course;
import jakarta.persistence.*;

import lombok.*;

import java.util.UUID;
@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name="first_name",nullable = false)
    String firstName;

    @Column(name="last_name",nullable = false)
    String lastName;

    @Column(nullable = false, unique = true)
    String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

}
