package com.example.slmback.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "attendance")
public class Attendance {

    @Id
    private String id;
    private String courseId;
    private String studentId;
    private String studentName;
    private String tutorId;
    private LocalDate date;
    private String status;         // PRESENT or ABSENT
    private String markedBy;       // TUTOR or SYSTEM
    private LocalDateTime markedAt = LocalDateTime.now();

    public String getId()                       { return id; }
    public void setId(String id)                { this.id = id; }
    public String getCourseId()                 { return courseId; }
    public void setCourseId(String c)           { this.courseId = c; }
    public String getStudentId()                { return studentId; }
    public void setStudentId(String s)          { this.studentId = s; }
    public String getStudentName()              { return studentName; }
    public void setStudentName(String s)        { this.studentName = s; }
    public String getTutorId()                  { return tutorId; }
    public void setTutorId(String t)            { this.tutorId = t; }
    public LocalDate getDate()                  { return date; }
    public void setDate(LocalDate d)            { this.date = d; }
    public String getStatus()                   { return status; }
    public void setStatus(String s)             { this.status = s; }
    public String getMarkedBy()                 { return markedBy; }
    public void setMarkedBy(String m)           { this.markedBy = m; }
    public LocalDateTime getMarkedAt()          { return markedAt; }
    public void setMarkedAt(LocalDateTime t)    { this.markedAt = t; }
}