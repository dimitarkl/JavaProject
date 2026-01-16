package com.example.demo.attendance;

import com.example.demo.attendance.dtos.AttendanceData;
import com.example.demo.attendance.dtos.AttendanceResponse;
import com.example.demo.attendance.dtos.AttendancesByLessonResponse;
import com.example.demo.attendance.service.AttendanceService;
import com.example.demo.auth.Role;
import com.example.demo.auth.User;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.student.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/{id}/verify")
    public ResponseEntity<AttendanceResponse> verifyAttendance(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {

        if (currentUser.getRole() == Role.TEACHER)
            throw new UnauthorizedException("Only students can verify attendance");

        Student student = (Student) currentUser;

        AttendanceData attendanceData = new AttendanceData();
        attendanceData.setLessonId(id);
        attendanceData.setStudentId(student.getId());

        return new ResponseEntity<>(attendanceService.recordAttendance(attendanceData), HttpStatus.CREATED);
    }

    //TODO Can implement Roles
    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<AttendancesByLessonResponse> getAttendancesByLessonId(
            @PathVariable UUID lessonId,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() == Role.STUDENT)
            throw new UnauthorizedException("Only teacher can access attendances");

        return ResponseEntity.ok(attendanceService.getAttendancesByLessonId(lessonId));
    }


}
