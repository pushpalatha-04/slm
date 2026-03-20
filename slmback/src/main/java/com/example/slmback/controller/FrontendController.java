package com.example.slmback.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FrontendController {

    @GetMapping(value = "/login.html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Resource> login() {
        Resource resource = new ClassPathResource("static/login.html");
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(resource);
    }

    @GetMapping(value = "/courses.html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Resource> courses() {
        Resource resource = new ClassPathResource("static/courses.html");
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(resource);
    }

    @GetMapping(value = "/course.html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Resource> course() {
        Resource resource = new ClassPathResource("static/course.html");
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(resource);
    }

    @GetMapping(value = "/quiz.html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Resource> quiz() {
        Resource resource = new ClassPathResource("static/quiz.html");
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(resource);
    }

    @GetMapping(value = "/coding.html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Resource> coding() {
        Resource resource = new ClassPathResource("static/coding.html");
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(resource);
    }

    @GetMapping(value = "/student-dashboard.html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Resource> studentDashboard() {
        Resource resource = new ClassPathResource("static/student-dashboard.html");
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(resource);
    }

    @GetMapping(value = "/tutor-dashboard.html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Resource> tutorDashboard() {
        Resource resource = new ClassPathResource("static/tutor-dashboard.html");
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(resource);
    }

    @GetMapping(value = "/topics.html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Resource> topics() {
        Resource resource = new ClassPathResource("static/topics.html");
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(resource);
    }
}