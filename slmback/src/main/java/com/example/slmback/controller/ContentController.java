package com.example.slmback.controller;

import com.example.slmback.model.CourseContent;
import com.example.slmback.repository.CourseContentRepository;
import com.example.slmback.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/content")
@CrossOrigin("*")
public class ContentController {

    @Autowired private CourseContentRepository contentRepo;
    @Autowired private JwtUtil                 jwtUtil;

    // ── Get content for a course ──────────────────────────────
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Map<String, Object>> getCourseContent(
            @PathVariable String courseId) {
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("content", contentRepo.findByCourseIdOrderByOrderIndexAsc(courseId));
        return ResponseEntity.ok(res);
    }

    // ── Add any content (VIDEO, PDF, QUIZ, CODING) ────────────
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addContent(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, Object> req) {

        Map<String, Object> res = new HashMap<>();
        String tutorId = getUserId(auth);
        String courseId = (String) req.get("courseId");
        String type     = (String) req.get("type");
        String title    = (String) req.getOrDefault("title", "");

        if (courseId == null || type == null || title.isBlank()) {
            res.put("success", false);
            res.put("message", "courseId, type and title are required.");
            return ResponseEntity.badRequest().body(res);
        }

        CourseContent c = new CourseContent();
        c.setCourseId(courseId);
        c.setTutorId(tutorId);
        c.setType(type);
        c.setTitle(title);
        c.setDescription((String) req.getOrDefault("description", ""));
        c.setOrderIndex(getNextOrder(courseId));

        switch (type) {
            case "VIDEO":
            case "PDF":
                // Tutor pastes the URL (YouTube link or Google Drive PDF link)
                c.setFileUrl((String) req.getOrDefault("fileUrl", ""));
                break;

            case "QUIZ":
                List<Map<String, String>> rawQs = (List<Map<String, String>>) req.get("questions");
                if (rawQs != null) {
                    List<CourseContent.QuizQuestion> qs = new ArrayList<>();
                    rawQs.forEach(rq -> {
                        CourseContent.QuizQuestion q = new CourseContent.QuizQuestion();
                        q.setQuestion(rq.get("question"));
                        q.setOptionA(rq.get("optionA"));
                        q.setOptionB(rq.get("optionB"));
                        q.setOptionC(rq.get("optionC"));
                        q.setOptionD(rq.get("optionD"));
                        q.setCorrectAnswer(rq.get("correctAnswer"));
                        qs.add(q);
                    });
                    c.setQuestions(qs);
                }
                break;

            case "CODING":
                c.setProblemStatement((String) req.getOrDefault("problemStatement", ""));
                c.setStarterCode((String) req.getOrDefault("starterCode", ""));
                c.setLanguage((String) req.getOrDefault("language", "java"));
                break;
        }

        contentRepo.save(c);
        res.put("success", true);
        res.put("content", c);
        return ResponseEntity.ok(res);
    }

    // ── Delete content ────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteContent(
            @RequestHeader("Authorization") String auth,
            @PathVariable String id) {
        Map<String, Object> res = new HashMap<>();
        String tutorId = getUserId(auth);
        Optional<CourseContent> opt = contentRepo.findById(id);
        if (opt.isEmpty() || !opt.get().getTutorId().equals(tutorId)) {
            res.put("success", false); res.put("message", "Not found or unauthorized");
            return ResponseEntity.status(403).body(res);
        }
        contentRepo.deleteById(id);
        res.put("success", true);
        return ResponseEntity.ok(res);
    }

    // ── Helpers ───────────────────────────────────────────────
    private String getUserId(String auth) {
        try { return jwtUtil.extractUserId(auth.replace("Bearer ", "")); }
        catch (Exception e) { return null; }
    }
    private int getNextOrder(String courseId) {
        List<CourseContent> list = contentRepo.findByCourseIdOrderByOrderIndexAsc(courseId);
        return list.isEmpty() ? 0 : list.get(list.size() - 1).getOrderIndex() + 1;
    }
}