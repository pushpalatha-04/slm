package com.example.slmback.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "messages")
public class Message {

    @Id
    private String id;
    private String courseId;
    private String senderId;
    private String senderName;
    private String senderRole;
    private String receiverId;
    private String text;
    private boolean read = false;
    private LocalDateTime sentAt = LocalDateTime.now();

    public String getId()                       { return id; }
    public void setId(String id)                { this.id = id; }
    public String getCourseId()                 { return courseId; }
    public void setCourseId(String c)           { this.courseId = c; }
    public String getSenderId()                 { return senderId; }
    public void setSenderId(String s)           { this.senderId = s; }
    public String getSenderName()               { return senderName; }
    public void setSenderName(String s)         { this.senderName = s; }
    public String getSenderRole()               { return senderRole; }
    public void setSenderRole(String s)         { this.senderRole = s; }
    public String getReceiverId()               { return receiverId; }
    public void setReceiverId(String r)         { this.receiverId = r; }
    public String getText()                     { return text; }
    public void setText(String t)               { this.text = t; }
    public boolean isRead()                     { return read; }
    public void setRead(boolean r)              { this.read = r; }
    public LocalDateTime getSentAt()            { return sentAt; }
    public void setSentAt(LocalDateTime t)      { this.sentAt = t; }
}