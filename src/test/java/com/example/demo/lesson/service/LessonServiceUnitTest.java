package com.example.demo.lesson.service;

import com.example.demo.lesson.model.Lesson;
import com.example.demo.lesson.repository.LessonRepository;
import com.example.demo.subject.model.Subject;
import com.example.demo.subject.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LessonService Unit Tests")
class LessonServiceUnitTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private LessonService lessonService;

    private Lesson futureLesson;
    private Lesson pastLesson;

    @BeforeEach
    void setUp() {
        Subject subject = Subject.builder().id(UUID.randomUUID()).name("Math").build();

        futureLesson = Lesson.builder()
                .id(UUID.randomUUID())
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(1).plusHours(2))
                .subject(subject)
                .build();

        pastLesson = Lesson.builder()
                .id(UUID.randomUUID())
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().minusDays(1).plusHours(2))
                .subject(subject)
                .build();
    }

    @Test
    @DisplayName("Should return only future lessons")
    void testGetFutureLessons() {
        // Казваме на мока: "Когато те питат за всички, върни този списък"
        // Забележка: Тук тестваме логиката за филтриране, ако тя е в Java кода.
        // Ако логиката е в Repository (@Query), този unit test е по-малко полезен от интеграционния.
        // Но ето как става:

        when(lessonRepository.findAllFutureLessons(any(LocalDateTime.class)))
                .thenReturn(List.of(futureLesson));

        List<Lesson> result = lessonService.getFutureLessons();

        assertEquals(1, result.size());
        assertEquals(futureLesson.getId(), result.get(0).getId());

        verify(lessonRepository, times(1)).findAllFutureLessons(any(LocalDateTime.class));
    }
}