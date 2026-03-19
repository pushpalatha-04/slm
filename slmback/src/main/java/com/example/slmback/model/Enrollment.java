package com.example.slmback.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import java.time.LocalDateTime;

@Document(collection = "enrollments")
@CompoundIndex(name = "student_course_idx", def = "{'studentId':1,'courseId':1}", unique = true)
public class Enrollment {

    @Id
    private String id;
    private String studentId;
    private String studentName;
    private String courseId;
    private String courseTitle;
    private String tutorId;
    private LocalDateTime enrolledAt = LocalDateTime.now();

    public String getId()                       { return id; }
    public void setId(String id)                { this.id = id; }
    public String getStudentId()                { return studentId; }
    public void setStudentId(String s)          { this.studentId = s; }
    public String getStudentName()              { return studentName; }
    public void setStudentName(String s)        { this.studentName = s; }
    public String getCourseId()                 { return courseId; }
    public void setCourseId(String c)           { this.courseId = c; }
    public String getCourseTitle()              { return courseTitle; }
    public void setCourseTitle(String c)        { this.courseTitle = c; }
    public String getTutorId()                  { return tutorId; }
    public void setTutorId(String t)            { this.tutorId = t; }
    public LocalDateTime getEnrolledAt()        { return enrolledAt; }
    public void setEnrolledAt(LocalDateTime t)  { this.enrolledAt = t; }
}