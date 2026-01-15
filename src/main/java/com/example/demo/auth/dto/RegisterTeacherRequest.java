package com.example.demo.auth.dto;

import lombok.Data;

@Data
public class RegisterTeacherRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}