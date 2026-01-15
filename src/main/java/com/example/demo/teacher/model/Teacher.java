package com.example.demo.teacher.model;
import com.example.demo.auth.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "teacher")
@Getter
@Setter
public class Teacher extends User {

    //TODO All other relations
}
