package com.example.slmback.service;

import com.example.slmback.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {

    @Autowired
    private GeminiService geminiService;

    public List<Question> generateQuiz(String course) {
        return geminiService.generateQuestions(course);
    }
}