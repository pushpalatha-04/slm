package com.example.slmback.controller;

import com.example.slmback.model.*;
import com.example.slmback.repository.*;
import com.example.slmback.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/submissions")
@CrossOrigin("*")
public class SubmissionController {

    @Autowired private SubmissionRepository    subRepo;
    @Autowired private CourseContentRepository contentRepo;
    @Autowired private UserRepository          userRepo;
    @Autowired private JwtUtil                 jwtUtil;

    // STUDENT: submit quiz answers
    @PostMapping("/quiz")
    public ResponseEntity<Map<String, Object>> submitQuiz(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, Object> req) {

        Map<String, Object> res = new HashMap<>();
        String studentId = getUserId(auth);
        String contentId = (String) req.get("contentId");
        String courseId  = (String) req.get("courseId");
        Map<String, String> answers = (Map<String, String>) req.get("answers");

        Optional<CourseContent> contentOpt = contentRepo.findById(contentId);
        if (contentOpt.isEmpty()) {
            res.put("success", false); res.put("message", "Quiz not found");
            return ResponseEntity.badRequest().body(res);
        }

        List<CourseContent.QuizQuestion> questions = contentOpt.get().getQuestions();
        int correct = 0;
        for (int i = 0; i < questions.size(); i++) {
            String ans = answers != null ? answers.get(String.valueOf(i)) : null;
            if (ans != null && ans.equals(questions.get(i).getCorrectAnswer())) correct++;
        }

        String studentName = userRepo.findById(studentId)
            .map(u -> u.getFullName() != null ? u.getFullName() : u.getUsername())
            .orElse("Student");

        Submission sub = new Submission();
        sub.setStudentId(studentId);
        sub.setStudentName(studentName);
        sub.setCourseId(courseId);
        sub.setContentId(contentId);
        sub.setType("QUIZ");
        sub.setAnswers(answers);
        sub.setScore(correct);
        sub.setTotal(questions.size());
        sub.setPassed(questions.size() > 0 && correct >= questions.size() * 0.6);
        subRepo.save(sub);

        res.put("success", true);
        res.put("score",   correct);
        res.put("total",   questions.size());
        res.put("passed",  sub.isPassed());
        return ResponseEntity.ok(res);
    }

    // STUDENT: submit coding test
    @PostMapping("/coding")
    public ResponseEntity<Map<String, Object>> submitCoding(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, String> req) {

        Map<String, Object> res = new HashMap<>();
        String studentId = getUserId(auth);
        String studentName = userRepo.findById(studentId)
            .map(u -> u.getFullName() != null ? u.getFullName() : u.getUsername())
            .orElse("Student");

        Submission sub = new Submission();
        sub.setStudentId(studentId);
        sub.setStudentName(studentName);
        sub.setCourseId(req.get("courseId"));
        sub.setContentId(req.get("contentId"));
        sub.setType("CODING");
        sub.setCode(req.get("code"));
        sub.setLanguage(req.getOrDefault("language", "java"));
        sub.setOutput(req.getOrDefault("output", ""));
        sub.setStatus("SUBMITTED");
        subRepo.save(sub);

        res.put("success", true);
        res.put("message", "Code submitted! Your tutor will review it.");
        return ResponseEntity.ok(res);
    }

    // TUTOR: get all submissions for a course
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Map<String, Object>> getCourseSubmissions(
            @PathVariable String courseId) {
        Map<String, Object> res = new HashMap<>();
        res.put("success",     true);
        res.put("submissions", subRepo.findByCourseId(courseId));
        return ResponseEntity.ok(res);
    }

    // TUTOR: add feedback on a coding submission
    @PutMapping("/{id}/feedback")
    public ResponseEntity<Map<String, Object>> addFeedback(
            @PathVariable String id,
            @RequestBody Map<String, String> req) {
        Map<String, Object> res = new HashMap<>();
        subRepo.findById(id).ifPresent(s -> {
            s.setTutorFeedback(req.get("feedback"));
            s.setStatus("REVIEWED");
            subRepo.save(s);
        });
        res.put("success", true);
        return ResponseEntity.ok(res);
    }

    // STUDENT: get my submissions in a course
    @GetMapping("/my/{courseId}")
    public ResponseEntity<Map<String, Object>> getMySubmissions(
            @RequestHeader("Authorization") String auth,
            @PathVariable String courseId) {
        Map<String, Object> res = new HashMap<>();
        String studentId = getUserId(auth);
        res.put("success",     true);
        res.put("submissions", subRepo.findByStudentIdAndCourseId(studentId, courseId));
        return ResponseEntity.ok(res);
    }

    private String getUserId(String auth) {
        try { return jwtUtil.extractUserId(auth.replace("Bearer ", "")); }
        catch (Exception e) { return null; }
    }
}