package com.example.slmback.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "courses")
public class Course {

    @Id
    private String id;
    private String tutorId;
    private String tutorName;
    private String title;
    private String description;
    private String subject;
    private String enrollCode;
    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    public String getId()                       { return id; }
    public void setId(String id)                { this.id = id; }
    public String getTutorId()                  { return tutorId; }
    public void setTutorId(String t)            { this.tutorId = t; }
    public String getTutorName()                { return tutorName; }
    public void setTutorName(String t)          { this.tutorName = t; }
    public String getTitle()                    { return title; }
    public void setTitle(String t)              { this.title = t; }
    public String getDescription()              { return description; }
    public void setDescription(String d)        { this.description = d; }
    public String getSubject()                  { return subject; }
    public void setSubject(String s)            { this.subject = s; }
    public String getEnrollCode()               { return enrollCode; }
    public void setEnrollCode(String e)         { this.enrollCode = e; }
    public boolean isActive()                   { return active; }
    public void setActive(boolean a)            { this.active = a; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime t)   { this.createdAt = t; }
}