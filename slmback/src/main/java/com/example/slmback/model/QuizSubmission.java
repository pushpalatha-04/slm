package com.example.slmback.model;

import java.util.Map;

public class QuizSubmission {

    private Map<Integer, String> answers;

    public QuizSubmission() {}

    public QuizSubmission(Map<Integer, String> answers) {
        this.answers = answers;
    }

    public Map<Integer, String> getAnswers() { return answers; }
    public void setAnswers(Map<Integer, String> answers) { this.answers = answers; }
}