package com.example.demo.lesson.service;

import com.example.demo.lesson.dto.LessonRequest;
import com.example.demo.lesson.dto.LessonResponse;
import com.example.demo.lesson.model.Lesson;
import com.example.demo.lesson.repository.LessonRepository;
import com.example.demo.subject.model.Subject;
import com.example.demo.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final SubjectRepository subjectRepository;

    public LessonResponse createLesson(LessonRequest request) {
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found!"));

        Lesson lesson = Lesson.builder()
                .subject(subject)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .type(request.type())
                .build();

        Lesson savedLesson = lessonRepository.save(lesson);
        return mapToResponse(savedLesson);
    }

    public List<Lesson> getFutureLessons() {
        return lessonRepository.findAllFutureLessons(LocalDateTime.now());
    }

    public LessonResponse getLessonById(UUID id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        return mapToResponse(lesson);
    }

    private LessonResponse mapToResponse(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getSubject().getName(),
                lesson.getSubject().getTeacher().getLastName(),
                lesson.getStartDate(),
                lesson.getEndDate()
        );
    }
}