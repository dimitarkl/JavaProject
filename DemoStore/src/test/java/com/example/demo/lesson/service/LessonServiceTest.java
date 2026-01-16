package com.example.demo.lesson.service;

import com.example.demo.lesson.model.Lesson;
import com.example.demo.lesson.repository.LessonRepository;
import com.example.demo.subject.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private LessonService lessonService;

    @Test
    void shouldReturnFutureLessons() {
        Lesson lesson = new Lesson();
        lesson.setStartDate(LocalDateTime.now().plusDays(1)); // Утре

        when(lessonRepository.findAllFutureLessons(any())).thenReturn(List.of(lesson));

        List<Lesson> result = lessonService.getFutureLessons();

        assertEquals(1, result.size());
    }
}