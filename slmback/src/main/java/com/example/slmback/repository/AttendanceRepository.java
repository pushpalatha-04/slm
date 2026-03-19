package com.example.slmback.repository;

import com.example.slmback.model.Attendance;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends MongoRepository<Attendance, String> {
    List<Attendance> findByCourseId(String courseId);
    List<Attendance> findByCourseIdAndDate(String courseId, LocalDate date);
    List<Attendance> findByStudentIdAndCourseId(String studentId, String courseId);
    Optional<Attendance> findByStudentIdAndCourseIdAndDate(String studentId, String courseId, LocalDate date);
}