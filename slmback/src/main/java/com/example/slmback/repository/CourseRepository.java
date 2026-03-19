package com.example.slmback.repository;

import com.example.slmback.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findByTutorId(String tutorId);
    Optional<Course> findByEnrollCode(String enrollCode);
}