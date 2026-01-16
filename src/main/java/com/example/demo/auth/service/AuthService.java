package com.example.demo.auth.service;

import com.example.demo.auth.dto.AuthTokens;
import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.RegisterStudentRequest;
import com.example.demo.auth.dto.RegisterTeacherRequest;
import com.example.demo.config.JwtService;
import com.example.demo.auth.Role;
import com.example.demo.auth.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.course.model.Course;
import com.example.demo.course.service.CourseService;
import com.example.demo.student.model.Student;
import com.example.demo.student.repository.StudentRepository;
import com.example.demo.teacher.model.Teacher;
import com.example.demo.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CourseService courseService;

    public AuthTokens registerStudent(RegisterStudentRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Invalid email or password");
        }
        Course course = courseService.getCourseEntity(request.getCourseId());

        Student student = Student.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STUDENT)
                .course(course)
                .build();

        studentRepository.save(student);

        String accessToken = jwtService.generateAccessToken(student);
        String refreshToken = jwtService.generateRefreshToken(student);
        return new AuthTokens(accessToken, refreshToken);
    }

    public AuthTokens registerTeacher(RegisterTeacherRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Invalid email or password");
        }

        Teacher teacher = Teacher.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Encrypt inside builder
                .role(Role.TEACHER)
                .build();

        teacherRepository.save(teacher);

        String accessToken = jwtService.generateAccessToken(teacher);
        String refreshToken = jwtService.generateRefreshToken(teacher);
        return new AuthTokens(accessToken, refreshToken);
    }

    public AuthTokens login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthTokens(accessToken, refreshToken);
    }

    public AuthTokens refresh(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        return new AuthTokens(newAccessToken, newRefreshToken);
    }
}
