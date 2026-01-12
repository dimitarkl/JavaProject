package com.example.demo.student.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    UUID id;

    @Column(name="first_name",nullable = false)
    String firstName;

    @Column(name="last_name",nullable = false)
    String lastName;

    @Column(nullable = false, unique = true)
    String email;

    //FACULTYID
}
