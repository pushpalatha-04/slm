package com.example.slmback.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "submissions")
public class Submission {

    @Id
    private String id;
    private String studentId;
    private String studentName;
    private String courseId;
    private String contentId;
    private String type;           // QUIZ or CODING
    private Map<String, String> answers;
    private int score;
    private int total;
    private boolean passed;
    private String code;
    private String language;
    private String output;
    private String status = "SUBMITTED";
    private String tutorFeedback;
    private LocalDateTime submittedAt = LocalDateTime.now();

    public String getId()                           { return id; }
    public void setId(String id)                    { this.id = id; }
    public String getStudentId()                    { return studentId; }
    public void setStudentId(String s)              { this.studentId = s; }
    public String getStudentName()                  { return studentName; }
    public void setStudentName(String s)            { this.studentName = s; }
    public String getCourseId()                     { return courseId; }
    public void setCourseId(String c)               { this.courseId = c; }
    public String getContentId()                    { return contentId; }
    public void setContentId(String c)              { this.contentId = c; }
    public String getType()                         { return type; }
    public void setType(String t)                   { this.type = t; }
    public Map<String, String> getAnswers()         { return answers; }
    public void setAnswers(Map<String, String> a)   { this.answers = a; }
    public int getScore()                           { return score; }
    public void setScore(int s)                     { this.score = s; }
    public int getTotal()                           { return total; }
    public void setTotal(int t)                     { this.total = t; }
    public boolean isPassed()                       { return passed; }
    public void setPassed(boolean p)                { this.passed = p; }
    public String getCode()                         { return code; }
    public void setCode(String c)                   { this.code = c; }
    public String getLanguage()                     { return language; }
    public void setLanguage(String l)               { this.language = l; }
    public String getOutput()                       { return output; }
    public void setOutput(String o)                 { this.output = o; }
    public String getStatus()                       { return status; }
    public void setStatus(String s)                 { this.status = s; }
    public String getTutorFeedback()                { return tutorFeedback; }
    public void setTutorFeedback(String f)          { this.tutorFeedback = f; }
    public LocalDateTime getSubmittedAt()           { return submittedAt; }
    public void setSubmittedAt(LocalDateTime t)     { this.submittedAt = t; }
}