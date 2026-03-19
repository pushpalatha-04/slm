package com.example.slmback.controller;

import com.example.slmback.model.Attendance;
import com.example.slmback.repository.AttendanceRepository;
import com.example.slmback.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin("*")
public class AttendanceController {

    @Autowired private AttendanceRepository attendanceRepo;
    @Autowired private JwtUtil              jwtUtil;

    // TUTOR: bulk mark attendance for all students on a date
    @PostMapping("/bulk-mark")
    public ResponseEntity<Map<String, Object>> bulkMark(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, Object> req) {

        Map<String, Object> res = new HashMap<>();
        String tutorId  = getUserId(auth);
        String courseId = (String) req.get("courseId");
        LocalDate date  = LocalDate.parse((String) req.getOrDefault("date", LocalDate.now().toString()));

        List<Map<String, String>> records = (List<Map<String, String>>) req.get("records");
        if (records == null) { res.put("success", false); res.put("message", "records required"); return ResponseEntity.badRequest().body(res); }

        records.forEach(r -> {
            Attendance a = attendanceRepo
                .findByStudentIdAndCourseIdAndDate(r.get("studentId"), courseId, date)
                .orElseGet(Attendance::new);
            a.setCourseId(courseId);
            a.setStudentId(r.get("studentId"));
            a.setStudentName(r.get("studentName"));
            a.setTutorId(tutorId);
            a.setDate(date);
            a.setStatus(r.get("status"));
            a.setMarkedBy("TUTOR");
            attendanceRepo.save(a);
        });

        res.put("success", true);
        res.put("message", "Attendance saved for " + records.size() + " students");
        return ResponseEntity.ok(res);
    }

    // TUTOR: get attendance for a course on a specific date
    @GetMapping("/course/{courseId}/date/{date}")
    public ResponseEntity<Map<String, Object>> getByDate(
            @PathVariable String courseId,
            @PathVariable String date) {
        Map<String, Object> res = new HashMap<>();
        res.put("success",    true);
        res.put("attendance", attendanceRepo.findByCourseIdAndDate(courseId, LocalDate.parse(date)));
        return ResponseEntity.ok(res);
    }

    // STUDENT: auto-mark present when they open course content
    @PostMapping("/auto")
    public ResponseEntity<Map<String, Object>> autoMark(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, String> req) {

        Map<String, Object> res = new HashMap<>();
        String studentId   = getUserId(auth);
        String courseId    = req.get("courseId");
        String studentName = req.getOrDefault("studentName", "Student");
        LocalDate today    = LocalDate.now();

        if (attendanceRepo.findByStudentIdAndCourseIdAndDate(studentId, courseId, today).isEmpty()) {
            Attendance a = new Attendance();
            a.setCourseId(courseId);
            a.setStudentId(studentId);
            a.setStudentName(studentName);
            a.setDate(today);
            a.setStatus("PRESENT");
            a.setMarkedBy("SYSTEM");
            attendanceRepo.save(a);
        }
        res.put("success", true);
        return ResponseEntity.ok(res);
    }

    // STUDENT: get own attendance for a course
    @GetMapping("/my/{courseId}")
    public ResponseEntity<Map<String, Object>> getMyAttendance(
            @RequestHeader("Authorization") String auth,
            @PathVariable String courseId) {
        Map<String, Object> res = new HashMap<>();
        String studentId = getUserId(auth);
        res.put("success",    true);
        res.put("attendance", attendanceRepo.findByStudentIdAndCourseId(studentId, courseId));
        return ResponseEntity.ok(res);
    }

    private String getUserId(String auth) {
        try { return jwtUtil.extractUserId(auth.replace("Bearer ", "")); }
        catch (Exception e) { return null; }
    }
}