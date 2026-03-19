package com.example.slmback.repository;

import com.example.slmback.model.Submission;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends MongoRepository<Submission, String> {
    List<Submission> findByCourseId(String courseId);
    List<Submission> findByStudentIdAndCourseId(String studentId, String courseId);
    List<Submission> findByContentId(String contentId);
    Optional<Submission> findByStudentIdAndContentId(String studentId, String contentId);
}