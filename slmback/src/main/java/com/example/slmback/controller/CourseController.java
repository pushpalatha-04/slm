package com.example.slmback.controller;

import com.example.slmback.model.*;
import com.example.slmback.repository.*;
import com.example.slmback.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin("*")
public class CourseController {

    @Autowired private CourseRepository     courseRepo;
    @Autowired private EnrollmentRepository enrollRepo;
    @Autowired private UserRepository       userRepo;
    @Autowired private JwtUtil              jwtUtil;

    // ── TUTOR: Create course ──────────────────────────────────
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createCourse(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, String> req) {

        Map<String, Object> res = new HashMap<>();
        if (!"TUTOR".equals(getRole(auth))) {
            res.put("success", false); res.put("message", "Tutors only");
            return ResponseEntity.status(403).body(res);
        }
        String tutorId = getUserId(auth);
        String tutorName = userRepo.findById(tutorId)
            .map(u -> u.getFullName() != null ? u.getFullName() : u.getUsername())
            .orElse("Tutor");

        Course course = new Course();
        course.setTutorId(tutorId);
        course.setTutorName(tutorName);
        course.setTitle(req.getOrDefault("title", ""));
        course.setDescription(req.getOrDefault("description", ""));
        course.setSubject(req.getOrDefault("subject", ""));
        course.setEnrollCode(generateCode());
        courseRepo.save(course);

        res.put("success", true);
        res.put("course",  toMap(course));
        res.put("message", "Course created! Enroll code: " + course.getEnrollCode());
        return ResponseEntity.ok(res);
    }

    // ── TUTOR: Get my courses ─────────────────────────────────
    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyCourses(
            @RequestHeader("Authorization") String auth) {
        Map<String, Object> res = new HashMap<>();
        String tutorId = getUserId(auth);
        List<Map<String, Object>> list = new ArrayList<>();
        courseRepo.findByTutorId(tutorId).forEach(c -> {
            Map<String, Object> cm = toMap(c);
            cm.put("studentCount", enrollRepo.findByCourseId(c.getId()).size());
            list.add(cm);
        });
        res.put("success", true); res.put("courses", list);
        return ResponseEntity.ok(res);
    }

    // ── TUTOR: Delete course ──────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCourse(
            @RequestHeader("Authorization") String auth,
            @PathVariable String id) {
        Map<String, Object> res = new HashMap<>();
        String tutorId = getUserId(auth);
        Optional<Course> opt = courseRepo.findById(id);
        if (opt.isEmpty() || !opt.get().getTutorId().equals(tutorId)) {
            res.put("success", false); res.put("message", "Not found or unauthorized");
            return ResponseEntity.status(403).body(res);
        }
        courseRepo.deleteById(id);
        res.put("success", true);
        return ResponseEntity.ok(res);
    }

    // ── STUDENT: Enroll by code ───────────────────────────────
    @PostMapping("/enroll")
    public ResponseEntity<Map<String, Object>> enroll(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, String> req) {
        Map<String, Object> res = new HashMap<>();
        String studentId = getUserId(auth);
        String code = req.getOrDefault("enrollCode", "").trim().toUpperCase();

        Optional<Course> courseOpt = courseRepo.findByEnrollCode(code);
        if (courseOpt.isEmpty()) {
            res.put("success", false); res.put("message", "Invalid enroll code.");
            return ResponseEntity.badRequest().body(res);
        }
        Course course = courseOpt.get();
        if (enrollRepo.existsByStudentIdAndCourseId(studentId, course.getId())) {
            res.put("success", false); res.put("message", "Already enrolled.");
            return ResponseEntity.badRequest().body(res);
        }
        String studentName = userRepo.findById(studentId)
            .map(u -> u.getFullName() != null ? u.getFullName() : u.getUsername())
            .orElse("Student");

        Enrollment e = new Enrollment();
        e.setStudentId(studentId);
        e.setStudentName(studentName);
        e.setCourseId(course.getId());
        e.setCourseTitle(course.getTitle());
        e.setTutorId(course.getTutorId());
        enrollRepo.save(e);

        res.put("success", true);
        res.put("course",  toMap(course));
        res.put("message", "Enrolled in " + course.getTitle() + "!");
        return ResponseEntity.ok(res);
    }

    // ── STUDENT: My enrolled courses ──────────────────────────
    @GetMapping("/enrolled")
    public ResponseEntity<Map<String, Object>> getEnrolled(
            @RequestHeader("Authorization") String auth) {
        Map<String, Object> res = new HashMap<>();
        String studentId = getUserId(auth);
        List<Map<String, Object>> list = new ArrayList<>();
        enrollRepo.findByStudentId(studentId).forEach(en ->
            courseRepo.findById(en.getCourseId()).ifPresent(c -> {
                Map<String, Object> cm = toMap(c);
                cm.put("enrolledAt", en.getEnrolledAt());
                list.add(cm);
            }));
        res.put("success", true); res.put("courses", list);
        return ResponseEntity.ok(res);
    }

    // ── Get students in course ────────────────────────────────
    @GetMapping("/{courseId}/students")
    public ResponseEntity<Map<String, Object>> getCourseStudents(
            @PathVariable String courseId) {
        Map<String, Object> res = new HashMap<>();
        List<Map<String, Object>> students = new ArrayList<>();
        enrollRepo.findByCourseId(courseId).forEach(e -> {
            Map<String, Object> m = new HashMap<>();
            m.put("studentId",   e.getStudentId());
            m.put("studentName", e.getStudentName());
            m.put("enrolledAt",  e.getEnrolledAt());
            students.add(m);
        });
        res.put("success", true); res.put("students", students);
        return ResponseEntity.ok(res);
    }

    // ── Helpers ───────────────────────────────────────────────
    private String getUserId(String auth) {
        try { return jwtUtil.extractUserId(auth.replace("Bearer ", "")); }
        catch (Exception e) { return null; }
    }
    private String getRole(String auth) {
        try { return jwtUtil.extractRole(auth.replace("Bearer ", "")); }
        catch (Exception e) { return null; }
    }
    private String generateCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < 6; i++) sb.append(chars.charAt(rand.nextInt(chars.length())));
        return sb.toString();
    }
    private Map<String, Object> toMap(Course c) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",          c.getId());
        m.put("title",       c.getTitle());
        m.put("description", c.getDescription());
        m.put("subject",     c.getSubject());
        m.put("tutorId",     c.getTutorId());
        m.put("tutorName",   c.getTutorName());
        m.put("enrollCode",  c.getEnrollCode());
        m.put("createdAt",   c.getCreatedAt());
        return m;
    }
}