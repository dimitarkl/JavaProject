package com.example.demo.teacher.model;

import com.example.demo.auth.User;
import com.example.demo.subject.model.Subject;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder; // <--- ВАЖНО: Нов import

import java.util.List;

@Entity
@Table(name = "teachers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Teacher extends User {

    // No id, email, password, firstName, lastName
    // coming automatically from User thanks to SuperBuilder

    @OneToMany(mappedBy = "teacher")
    private List<Subject> subjects;
}