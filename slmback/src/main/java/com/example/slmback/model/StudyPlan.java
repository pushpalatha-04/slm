package com.example.slmback.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "study_plans")
public class StudyPlan {

    @Id
    private String id;
    private String courseId;
    private String tutorId;
    private List<Week> weeks = new ArrayList<>();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public static class Week {
        private int    weekNumber;
        private String topic;
        private String description;
        private String resourceUrl;

        public int    getWeekNumber()              { return weekNumber; }
        public void   setWeekNumber(int w)         { this.weekNumber = w; }
        public String getTopic()                   { return topic; }
        public void   setTopic(String t)           { this.topic = t; }
        public String getDescription()             { return description; }
        public void   setDescription(String d)     { this.description = d; }
        public String getResourceUrl()             { return resourceUrl; }
        public void   setResourceUrl(String r)     { this.resourceUrl = r; }
    }

    public String getId()                        { return id; }
    public void   setId(String id)               { this.id = id; }
    public String getCourseId()                  { return courseId; }
    public void   setCourseId(String c)          { this.courseId = c; }
    public String getTutorId()                   { return tutorId; }
    public void   setTutorId(String t)           { this.tutorId = t; }
    public List<Week> getWeeks()                 { return weeks; }
    public void   setWeeks(List<Week> w)         { this.weeks = w; }
    public LocalDateTime getUpdatedAt()          { return updatedAt; }
    public void   setUpdatedAt(LocalDateTime t)  { this.updatedAt = t; }
} 
