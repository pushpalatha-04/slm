package com.example.slmback.controller;

import com.example.slmback.model.CourseContent;
import com.example.slmback.model.Question;
import com.example.slmback.repository.CourseContentRepository;
import com.example.slmback.service.GeminiService;
import com.example.slmback.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin("*")
public class QuizController {

    @Autowired private GeminiService          geminiService;
    @Autowired private CourseContentRepository contentRepo;
    @Autowired private JwtUtil                 jwtUtil;

    // ── Existing endpoint (keep as-is for default quiz page) ──
    @GetMapping("/generate")
    public List<Question> generateQuiz(
            @RequestParam(defaultValue = "Java") String course) {
        return geminiService.generateQuestions(course);
    }

    // ── NEW: Generate AI quiz and save directly into a course ──
    // POST /api/quiz/generate-for-course
    // Body: { "courseId": "...", "topic": "Java OOP", "title": "Week 2 Quiz" }
    @PostMapping("/generate-for-course")
    public ResponseEntity<Map<String, Object>> generateForCourse(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, String> req) {

        Map<String, Object> res = new HashMap<>();

        String courseId = req.get("courseId");
        String topic    = req.getOrDefault("topic", "").trim();
        String title    = req.getOrDefault("title", "").trim();

        // Validate inputs
        if (courseId == null || courseId.isBlank()) {
            res.put("success", false);
            res.put("message", "courseId is required.");
            return ResponseEntity.badRequest().body(res);
        }
        if (topic.isBlank()) {
            res.put("success", false);
            res.put("message", "Topic is required to generate quiz.");
            return ResponseEntity.badRequest().body(res);
        }
        if (title.isBlank()) {
            title = "AI Quiz: " + topic;
        }

        // Get tutorId from JWT
        String tutorId;
        try {
            tutorId = jwtUtil.extractUserId(auth.replace("Bearer ", ""));
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", "Invalid token.");
            return ResponseEntity.status(401).body(res);
        }

        // Call Gemini — this is a blocking call (same as existing quiz page)
        List<Question> questions;
        try {
            questions = geminiService.generateQuestions(topic);
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", "Gemini API error: " + e.getMessage());
            return ResponseEntity.status(500).body(res);
        }

        if (questions == null || questions.isEmpty()) {
            res.put("success", false);
            res.put("message", "Gemini returned no questions. Try a different topic.");
            return ResponseEntity.status(500).body(res);
        }

        // Map Question → CourseContent.QuizQuestion
        // Both models use identical field names so this is a direct copy
        List<CourseContent.QuizQuestion> quizQuestions = new ArrayList<>();
        for (Question q : questions) {
            CourseContent.QuizQuestion cq = new CourseContent.QuizQuestion();
            cq.setQuestion(q.getQuestion());
            cq.setOptionA(q.getOptionA());
            cq.setOptionB(q.getOptionB());
            cq.setOptionC(q.getOptionC());
            cq.setOptionD(q.getOptionD());
            cq.setCorrectAnswer(q.getCorrectAnswer());
            quizQuestions.add(cq);
        }

        // Build and save CourseContent
        CourseContent content = new CourseContent();
        content.setCourseId(courseId);
        content.setTutorId(tutorId);
        content.setType("QUIZ");
        content.setTitle(title);
        content.setDescription("AI-generated quiz on: " + topic + " (" + quizQuestions.size() + " questions)");
        content.setQuestions(quizQuestions);
        content.setOrderIndex(getNextOrder(courseId));
        contentRepo.save(content);

        res.put("success",       true);
        res.put("message",       "Generated " + quizQuestions.size() + " questions for \"" + title + "\"");
        res.put("questionCount", quizQuestions.size());
        res.put("contentId",     content.getId());
        return ResponseEntity.ok(res);
    }

    // ── Helper ────────────────────────────────────────────────
    private int getNextOrder(String courseId) {
        List<CourseContent> list = contentRepo.findByCourseIdOrderByOrderIndexAsc(courseId);
        return list.isEmpty() ? 0 : list.get(list.size() - 1).getOrderIndex() + 1;
    }
}