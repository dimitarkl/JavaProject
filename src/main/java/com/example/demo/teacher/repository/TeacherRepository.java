package com.example.demo.teacher.repository;

import com.example.demo.auth.User;
import com.example.demo.teacher.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
}
