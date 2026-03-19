package com.example.slmback.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Document(collection = "course_content")
public class CourseContent {

    @Id
    private String id;
    private String courseId;
    private String tutorId;
    private String type;           // PDF, VIDEO, QUIZ, CODING
    private String title;
    private String description;
    private String fileUrl;        // PDF or Video URL (link only — no upload)
    private List<QuizQuestion> questions = new ArrayList<>();
    private String problemStatement;
    private String starterCode;
    private String language;
    private int orderIndex = 0;
    private LocalDateTime createdAt = LocalDateTime.now();

    public static class QuizQuestion {
        private String question;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private String correctAnswer;

        public String getQuestion()             { return question; }
        public void setQuestion(String q)       { this.question = q; }
        public String getOptionA()              { return optionA; }
        public void setOptionA(String o)        { this.optionA = o; }
        public String getOptionB()              { return optionB; }
        public void setOptionB(String o)        { this.optionB = o; }
        public String getOptionC()              { return optionC; }
        public void setOptionC(String o)        { this.optionC = o; }
        public String getOptionD()              { return optionD; }
        public void setOptionD(String o)        { this.optionD = o; }
        public String getCorrectAnswer()        { return correctAnswer; }
        public void setCorrectAnswer(String a)  { this.correctAnswer = a; }
    }

    public String getId()                              { return id; }
    public void setId(String id)                       { this.id = id; }
    public String getCourseId()                        { return courseId; }
    public void setCourseId(String c)                  { this.courseId = c; }
    public String getTutorId()                         { return tutorId; }
    public void setTutorId(String t)                   { this.tutorId = t; }
    public String getType()                            { return type; }
    public void setType(String t)                      { this.type = t; }
    public String getTitle()                           { return title; }
    public void setTitle(String t)                     { this.title = t; }
    public String getDescription()                     { return description; }
    public void setDescription(String d)               { this.description = d; }
    public String getFileUrl()                         { return fileUrl; }
    public void setFileUrl(String f)                   { this.fileUrl = f; }
    public List<QuizQuestion> getQuestions()           { return questions; }
    public void setQuestions(List<QuizQuestion> q)     { this.questions = q; }
    public String getProblemStatement()                { return problemStatement; }
    public void setProblemStatement(String p)          { this.problemStatement = p; }
    public String getStarterCode()                     { return starterCode; }
    public void setStarterCode(String s)               { this.starterCode = s; }
    public String getLanguage()                        { return language; }
    public void setLanguage(String l)                  { this.language = l; }
    public int getOrderIndex()                         { return orderIndex; }
    public void setOrderIndex(int o)                   { this.orderIndex = o; }
    public LocalDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(LocalDateTime t)          { this.createdAt = t; }
}