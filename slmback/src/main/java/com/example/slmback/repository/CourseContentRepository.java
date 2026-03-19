package com.example.slmback.repository;

import com.example.slmback.model.CourseContent;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CourseContentRepository extends MongoRepository<CourseContent, String> {
    List<CourseContent> findByCourseIdOrderByOrderIndexAsc(String courseId);
    void deleteByCourseId(String courseId);
}