package com.example.slmback.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import java.time.LocalDateTime;

@Document(collection = "student_progress")
@CompoundIndex(name = "user_course_idx", def = "{'userId': 1, 'course': 1}", unique = true)
public class StudentProgress {

    @Id
    private String id;

    private String  userId;
    private String  course;         // "java", "dbms", "dsa", "webdev"
    private Integer videosWatched = 0;
    private Boolean practiceDone  = false;
    private Boolean quizDone      = false;
    private Integer quizScore     = 0;
    private Boolean assignDone    = false;
    private Boolean assessDone    = false;
    private Integer assessScore   = 0;
    private Boolean assessPass    = false;
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ── Getters & Setters ─────────────────────────────────────
    public String getId()                       { return id; }
    public void setId(String id)                { this.id = id; }
    public String getUserId()                   { return userId; }
    public void setUserId(String u)             { this.userId = u; }
    public String getCourse()                   { return course; }
    public void setCourse(String c)             { this.course = c; }
    public Integer getVideosWatched()           { return videosWatched; }
    public void setVideosWatched(Integer v)     { this.videosWatched = v; }
    public Boolean getPracticeDone()            { return practiceDone; }
    public void setPracticeDone(Boolean v)      { this.practiceDone = v; }
    public Boolean getQuizDone()                { return quizDone; }
    public void setQuizDone(Boolean v)          { this.quizDone = v; }
    public Integer getQuizScore()               { return quizScore; }
    public void setQuizScore(Integer v)         { this.quizScore = v; }
    public Boolean getAssignDone()              { return assignDone; }
    public void setAssignDone(Boolean v)        { this.assignDone = v; }
    public Boolean getAssessDone()              { return assessDone; }
    public void setAssessDone(Boolean v)        { this.assessDone = v; }
    public Integer getAssessScore()             { return assessScore; }
    public void setAssessScore(Integer v)       { this.assessScore = v; }
    public Boolean getAssessPass()              { return assessPass; }
    public void setAssessPass(Boolean v)        { this.assessPass = v; }
    public LocalDateTime getUpdatedAt()         { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t)   { this.updatedAt = t; }
}