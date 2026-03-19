package com.example.slmback.controller;

import com.example.slmback.model.StudyPlan;
import com.example.slmback.repository.StudyPlanRepository;
import com.example.slmback.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/studyplan")
@CrossOrigin("*")
public class StudyPlanController {

    @Autowired private StudyPlanRepository studyPlanRepo;
    @Autowired private JwtUtil             jwtUtil;

    // ── TUTOR: Save (create or replace) study plan for a course ──
    // POST /api/studyplan/save
    // Body: { "courseId": "...", "weeks": [ { "weekNumber":1, "topic":"...", "description":"...", "resourceUrl":"..." } ] }
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> save(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, Object> req) {

        Map<String, Object> res = new HashMap<>();
        String tutorId  = getUserId(auth);
        String courseId = (String) req.get("courseId");

        if (courseId == null || courseId.isBlank()) {
            res.put("success", false);
            res.put("message", "courseId is required.");
            return ResponseEntity.badRequest().body(res);
        }

        // Parse weeks from request
        List<Map<String, Object>> rawWeeks =
            (List<Map<String, Object>>) req.getOrDefault("weeks", new ArrayList<>());

        List<StudyPlan.Week> weeks = new ArrayList<>();
        for (Map<String, Object> rw : rawWeeks) {
            StudyPlan.Week w = new StudyPlan.Week();
            w.setWeekNumber(((Number) rw.getOrDefault("weekNumber", 1)).intValue());
            w.setTopic((String) rw.getOrDefault("topic", ""));
            w.setDescription((String) rw.getOrDefault("description", ""));
            w.setResourceUrl((String) rw.getOrDefault("resourceUrl", ""));
            weeks.add(w);
        }

        // Upsert — one plan per course
        StudyPlan plan = studyPlanRepo.findByCourseId(courseId)
            .orElseGet(() -> {
                StudyPlan np = new StudyPlan();
                np.setCourseId(courseId);
                np.setTutorId(tutorId);
                return np;
            });

        plan.setWeeks(weeks);
        plan.setUpdatedAt(LocalDateTime.now());
        studyPlanRepo.save(plan);

        res.put("success", true);
        res.put("message", "Study plan saved with " + weeks.size() + " weeks.");
        res.put("plan",    planToMap(plan));
        return ResponseEntity.ok(res);
    }

    // ── GET study plan for a course (both tutor and student) ──
    // GET /api/studyplan/course/{courseId}
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Map<String, Object>> getByCourse(
            @PathVariable String courseId) {

        Map<String, Object> res = new HashMap<>();
        Optional<StudyPlan> opt = studyPlanRepo.findByCourseId(courseId);

        if (opt.isEmpty()) {
            res.put("success", true);
            res.put("plan",    null);
            res.put("weeks",   new ArrayList<>());
            return ResponseEntity.ok(res);
        }

        StudyPlan plan = opt.get();
        res.put("success", true);
        res.put("plan",    planToMap(plan));
        res.put("weeks",   plan.getWeeks());
        return ResponseEntity.ok(res);
    }

    // ── TUTOR: Delete study plan for a course ──────────────────
    @DeleteMapping("/course/{courseId}")
    public ResponseEntity<Map<String, Object>> delete(
            @PathVariable String courseId) {
        Map<String, Object> res = new HashMap<>();
        studyPlanRepo.deleteByCourseId(courseId);
        res.put("success", true);
        return ResponseEntity.ok(res);
    }

    // ── Helpers ───────────────────────────────────────────────
    private String getUserId(String auth) {
        try { return jwtUtil.extractUserId(auth.replace("Bearer ", "")); }
        catch (Exception e) { return null; }
    }

    private Map<String, Object> planToMap(StudyPlan p) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",        p.getId());
        m.put("courseId",  p.getCourseId());
        m.put("weeks",     p.getWeeks());
        m.put("updatedAt", p.getUpdatedAt() != null ? p.getUpdatedAt().toString() : null);
        return m;
    }
}