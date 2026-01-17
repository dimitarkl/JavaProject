package com.example.demo.attendance;

import com.example.demo.attendance.dtos.AttendanceData;
import com.example.demo.attendance.dtos.AttendanceResponse;
import com.example.demo.attendance.dtos.AttendancesByLessonResponse;
import com.example.demo.attendance.service.AttendanceService;
import com.example.demo.auth.Role;
import com.example.demo.auth.User;
import com.example.demo.auth.UserPrincipal;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.student.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/{id}/verify")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<AttendanceResponse> verifyAttendance(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User currentUser = userPrincipal.getUser();

        Student student = (Student) currentUser;

        AttendanceData attendanceData = new AttendanceData();
        attendanceData.setLessonId(id);
        attendanceData.setStudentId(student.getId());

        return new ResponseEntity<>(attendanceService.recordAttendance(attendanceData), HttpStatus.CREATED);
    }

    @GetMapping("/{lessonId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AttendancesByLessonResponse> getAttendancesByLessonId(
            @PathVariable UUID lessonId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User currentUser = userPrincipal.getUser();

        return ResponseEntity.ok(attendanceService.getAttendancesByLessonId(lessonId));
    }

}
