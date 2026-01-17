package com.example.demo.lesson.service;

import com.example.demo.lesson.dto.LessonRequest;
import com.example.demo.lesson.dto.LessonResponse;
import com.example.demo.lesson.model.Lesson;
import com.example.demo.lesson.repository.LessonRepository;
import com.example.demo.subject.model.Subject;
import com.example.demo.subject.repository.SubjectRepository;
import com.example.demo.subject.service.SubjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final SubjectService subjectService;

    @Transactional
    public LessonResponse createLesson(LessonRequest request) {
        Subject subject = subjectService.getSubjectEntityById(request.subjectId());

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
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found"));
        return mapToResponse(lesson);
    }

    public Lesson getLessonEntityById(UUID id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found"));
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