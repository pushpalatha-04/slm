package com.example.slmback.controller;

import com.example.slmback.model.Message;
import com.example.slmback.repository.MessageRepository;
import com.example.slmback.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin("*")
public class ChatController {

    @Autowired private MessageRepository     messageRepo;
    @Autowired private JwtUtil               jwtUtil;
    @Autowired private SimpMessagingTemplate messagingTemplate;

    // Get chat history for a course
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Map<String, Object>> getMessages(
            @PathVariable String courseId) {
        Map<String, Object> res = new HashMap<>();
        res.put("success",  true);
        res.put("messages", messageRepo.findByCourseIdOrderBySentAtAsc(courseId));
        return ResponseEntity.ok(res);
    }

    // Send message via REST + broadcast via WebSocket
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, String> req) {

        Map<String, Object> res = new HashMap<>();
        String senderId   = getUserId(auth);
        String senderRole = getRole(auth);

        Message msg = new Message();
        msg.setCourseId(req.get("courseId"));
        msg.setSenderId(senderId);
        msg.setSenderName(req.getOrDefault("senderName", "User"));
        msg.setSenderRole(senderRole != null ? senderRole : "STUDENT");
        msg.setReceiverId(req.get("receiverId"));
        msg.setText(req.get("text"));
        messageRepo.save(msg);

        // Broadcast to all subscribers
        messagingTemplate.convertAndSend("/topic/chat/" + msg.getCourseId(), msg);

        res.put("success", true);
        res.put("message", msg);
        return ResponseEntity.ok(res);
    }

    // WebSocket handler — receives from /app/chat.send
    @MessageMapping("/chat.send")
    public void handleWebSocket(@Payload Map<String, String> payload) {
        Message msg = new Message();
        msg.setCourseId(payload.get("courseId"));
        msg.setSenderId(payload.get("senderId"));
        msg.setSenderName(payload.get("senderName"));
        msg.setSenderRole(payload.getOrDefault("senderRole", "STUDENT"));
        msg.setReceiverId(payload.get("receiverId"));
        msg.setText(payload.get("text"));
        messageRepo.save(msg);

        messagingTemplate.convertAndSend("/topic/chat/" + msg.getCourseId(), msg);
    }

    private String getUserId(String auth) {
        try { return jwtUtil.extractUserId(auth.replace("Bearer ", "")); }
        catch (Exception e) { return null; }
    }
    private String getRole(String auth) {
        try { return jwtUtil.extractRole(auth.replace("Bearer ", "")); }
        catch (Exception e) { return null; }
    }
}