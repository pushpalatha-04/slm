package com.example.slmback.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;   // bcrypt hashed
    private String role;       // "STUDENT" or "TUTOR"
    private String fullName;
    private LocalDateTime createdAt = LocalDateTime.now();

    // ── Getters & Setters ─────────────────────────────────────
    public String getId()                       { return id; }
    public void setId(String id)                { this.id = id; }

    public String getUsername()                 { return username; }
    public void setUsername(String u)           { this.username = u; }

    public String getEmail()                    { return email; }
    public void setEmail(String e)              { this.email = e; }

    public String getPassword()                 { return password; }
    public void setPassword(String p)           { this.password = p; }

    public String getRole()                     { return role; }
    public void setRole(String r)               { this.role = r; }

    public String getFullName()                 { return fullName; }
    public void setFullName(String f)           { this.fullName = f; }

    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime t)   { this.createdAt = t; }
}