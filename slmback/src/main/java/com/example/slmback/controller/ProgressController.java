package com.example.slmback.controller;

import com.example.slmback.model.StudentProgress;
import com.example.slmback.model.User;
import com.example.slmback.repository.StudentProgressRepository;
import com.example.slmback.repository.UserRepository;
import com.example.slmback.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/progress")
@CrossOrigin("*")
public class ProgressController {

    @Autowired private StudentProgressRepository progressRepo;
    @Autowired private UserRepository            userRepo;
    @Autowired private JwtUtil                   jwtUtil;

    // ── Save / update progress for a course ──────────────────
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveProgress(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> req) {

        Map<String, Object> res = new HashMap<>();
        String userId = getUserId(authHeader);
        if (userId == null) {
            res.put("success", false);
            res.put("message", "Unauthorized");
            return ResponseEntity.status(401).body(res);
        }

        String course = (String) req.get("course");
        if (course == null) {
            res.put("success", false);
            res.put("message", "Course is required");
            return ResponseEntity.badRequest().body(res);
        }

        // Find existing or create new progress document
        StudentProgress p = progressRepo.findByUserIdAndCourse(userId, course)
            .orElseGet(() -> {
                StudentProgress np = new StudentProgress();
                np.setUserId(userId);
                np.setCourse(course);
                return np;
            });

        if (req.containsKey("videosWatched")) p.setVideosWatched(((Number) req.get("videosWatched")).intValue());
        if (req.containsKey("practiceDone"))  p.setPracticeDone((Boolean) req.get("practiceDone"));
        if (req.containsKey("quizDone"))      p.setQuizDone((Boolean) req.get("quizDone"));
        if (req.containsKey("quizScore"))     p.setQuizScore(((Number) req.get("quizScore")).intValue());
        if (req.containsKey("assignDone"))    p.setAssignDone((Boolean) req.get("assignDone"));
        if (req.containsKey("assessDone"))    p.setAssessDone((Boolean) req.get("assessDone"));
        if (req.containsKey("assessScore"))   p.setAssessScore(((Number) req.get("assessScore")).intValue());
        if (req.containsKey("assessPass"))    p.setAssessPass((Boolean) req.get("assessPass"));
        p.setUpdatedAt(LocalDateTime.now());

        progressRepo.save(p);
        res.put("success", true);
        return ResponseEntity.ok(res);
    }

    // ── Get my progress (student) ─────────────────────────────
    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyProgress(
            @RequestHeader("Authorization") String authHeader) {

        Map<String, Object> res = new HashMap<>();
        String userId = getUserId(authHeader);
        if (userId == null) {
            res.put("success", false);
            return ResponseEntity.status(401).body(res);
        }

        List<StudentProgress> list = progressRepo.findByUserId(userId);
        Map<String, Object> byC = new HashMap<>();
        list.forEach(p -> byC.put(p.getCourse(), progressToMap(p)));

        res.put("success",  true);
        res.put("progress", byC);
        return ResponseEntity.ok(res);
    }

    // ── TUTOR: get all students with progress ─────────────────
    @GetMapping("/all-students")
    public ResponseEntity<Map<String, Object>> getAllStudents(
            @RequestHeader("Authorization") String authHeader) {

        Map<String, Object> res = new HashMap<>();
        if (!"TUTOR".equals(getRole(authHeader))) {
            res.put("success", false);
            res.put("message", "Access denied — tutors only");
            return ResponseEntity.status(403).body(res);
        }

        List<User> students = userRepo.findAll().stream()
            .filter(u -> "STUDENT".equals(u.getRole()))
            .toList();

        List<Map<String, Object>> result = new ArrayList<>();
        for (User s : students) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("userId",   s.getId());
            entry.put("username", s.getUsername());
            entry.put("fullName", s.getFullName());
            entry.put("email",    s.getEmail());
            Map<String, Object> byC = new HashMap<>();
            progressRepo.findByUserId(s.getId())
                .forEach(p -> byC.put(p.getCourse(), progressToMap(p)));
            entry.put("progress", byC);
            result.add(entry);
        }

        res.put("success",  true);
        res.put("students", result);
        return ResponseEntity.ok(res);
    }

    // ── Helpers ───────────────────────────────────────────────
    private String getUserId(String authHeader) {
        try { return jwtUtil.extractUserId(authHeader.replace("Bearer ", "")); }
        catch (Exception e) { return null; }
    }

    private String getRole(String authHeader) {
        try { return jwtUtil.extractRole(authHeader.replace("Bearer ", "")); }
        catch (Exception e) { return null; }
    }

    private Map<String, Object> progressToMap(StudentProgress p) {
        Map<String, Object> m = new HashMap<>();
        m.put("videosWatched", p.getVideosWatched());
        m.put("practiceDone",  p.getPracticeDone());
        m.put("quizDone",      p.getQuizDone());
        m.put("quizScore",     p.getQuizScore());
        m.put("assignDone",    p.getAssignDone());
        m.put("assessDone",    p.getAssessDone());
        m.put("assessScore",   p.getAssessScore());
        m.put("assessPass",    p.getAssessPass());
        m.put("updatedAt",     p.getUpdatedAt() != null ? p.getUpdatedAt().toString() : null);
        return m;
    }
}