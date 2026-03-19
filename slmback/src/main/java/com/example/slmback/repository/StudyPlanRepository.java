package com.example.slmback.repository;

import com.example.slmback.model.StudyPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface StudyPlanRepository extends MongoRepository<StudyPlan, String> {
    Optional<StudyPlan> findByCourseId(String courseId);
    void deleteByCourseId(String courseId);
}