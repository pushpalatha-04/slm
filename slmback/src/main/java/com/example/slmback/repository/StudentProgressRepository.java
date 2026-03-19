package com.example.slmback.repository;

import com.example.slmback.model.StudentProgress;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface StudentProgressRepository extends MongoRepository<StudentProgress, String> {
    Optional<StudentProgress> findByUserIdAndCourse(String userId, String course);
    List<StudentProgress> findByUserId(String userId);
    List<StudentProgress> findByCourse(String course);
}