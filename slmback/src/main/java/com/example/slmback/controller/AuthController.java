package com.example.slmback.controller;

import com.example.slmback.model.User;
import com.example.slmback.repository.UserRepository;
import com.example.slmback.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired private UserRepository  userRepository;
    @Autowired private JwtUtil         jwtUtil;
    @Autowired private PasswordEncoder passwordEncoder;

    // ── SIGNUP ───────────────────────────────────────────────
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody Map<String, String> req) {
        Map<String, Object> res = new HashMap<>();

        String username = req.getOrDefault("username", "").trim();
        String email    = req.getOrDefault("email",    "").trim();
        String password = req.getOrDefault("password", "").trim();
        String role     = req.getOrDefault("role", "STUDENT").toUpperCase();
        String fullName = req.getOrDefault("fullName", username);

        // Validate
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            res.put("success", false);
            res.put("message", "Username, email and password are required.");
            return ResponseEntity.badRequest().body(res);
        }
        if (password.length() < 6) {
            res.put("success", false);
            res.put("message", "Password must be at least 6 characters.");
            return ResponseEntity.badRequest().body(res);
        }
        if (!role.equals("STUDENT") && !role.equals("TUTOR")) {
            res.put("success", false);
            res.put("message", "Role must be STUDENT or TUTOR.");
            return ResponseEntity.badRequest().body(res);
        }
        if (userRepository.existsByUsername(username)) {
            res.put("success", false);
            res.put("message", "Username already taken. Choose another.");
            return ResponseEntity.badRequest().body(res);
        }
        if (userRepository.existsByEmail(email)) {
            res.put("success", false);
            res.put("message", "Email already registered.");
            return ResponseEntity.badRequest().body(res);
        }

        // Save user — MongoDB auto-generates String _id
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setFullName(fullName);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());
        res.put("success",  true);
        res.put("token",    token);
        res.put("role",     user.getRole());
        res.put("username", user.getUsername());
        res.put("fullName", user.getFullName());
        res.put("userId",   user.getId());
        res.put("message",  "Account created successfully!");
        return ResponseEntity.ok(res);
    }

    // ── LOGIN ────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> req) {
        Map<String, Object> res = new HashMap<>();

        String username = req.getOrDefault("username", "").trim();
        String password = req.getOrDefault("password", "").trim();

        if (username.isEmpty() || password.isEmpty()) {
            res.put("success", false);
            res.put("message", "Username and password are required.");
            return ResponseEntity.badRequest().body(res);
        }

        // Allow login with username OR email
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) userOpt = userRepository.findByEmail(username);

        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            res.put("success", false);
            res.put("message", "Invalid username or password.");
            return ResponseEntity.status(401).body(res);
        }

        User user = userOpt.get();
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());
        res.put("success",  true);
        res.put("token",    token);
        res.put("role",     user.getRole());
        res.put("username", user.getUsername());
        res.put("fullName", user.getFullName());
        res.put("userId",   user.getId());
        res.put("message",  "Login successful!");
        return ResponseEntity.ok(res);
    }

    // ── VERIFY TOKEN ─────────────────────────────────────────
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> res = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            res.put("valid", false);
            return ResponseEntity.ok(res);
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            res.put("valid", false);
            return ResponseEntity.ok(res);
        }

        Optional<User> userOpt = userRepository.findByUsername(jwtUtil.extractUsername(token));
        if (userOpt.isEmpty()) {
            res.put("valid", false);
            return ResponseEntity.ok(res);
        }

        User user = userOpt.get();
        res.put("valid",    true);
        res.put("role",     user.getRole());
        res.put("username", user.getUsername());
        res.put("fullName", user.getFullName());
        res.put("userId",   user.getId());
        return ResponseEntity.ok(res);
    }
}