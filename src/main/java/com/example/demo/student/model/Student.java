package com.example.demo.student.model;
import com.example.demo.auth.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "student")
@Getter
@Setter
public class Student extends User {

    //FACULTYID
    //private String major;

}
