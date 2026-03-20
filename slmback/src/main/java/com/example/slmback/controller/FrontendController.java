package com.example.slmback.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/Login.html")
    public String login() {
        return "forward:/Login.html";
    }

    @GetMapping("/courses.html")
    public String courses() {
        return "forward:/courses.html";
    }

    @GetMapping("/course.html")
    public String course() {
        return "forward:/course.html";
    }

    @GetMapping("/quiz.html")
    public String quiz() {
        return "forward:/quiz.html";
    }

    @GetMapping("/coding.html")
    public String coding() {
        return "forward:/coding.html";
    }

    @GetMapping("/student-dashboard.html")
    public String studentDashboard() {
        return "forward:/student-dashboard.html";
    }

    @GetMapping("/tutor-dashboard.html")
    public String tutorDashboard() {
        return "forward:/tutor-dashboard.html";
    }

    @GetMapping("/topics.html")
    public String topics() {
        return "forward:/topics.html";
    }
}