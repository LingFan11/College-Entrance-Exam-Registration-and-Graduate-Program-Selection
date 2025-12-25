package com.lingfan.xspp.grad.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "grad_session")
public class GradSession {
    @Id
    @Column(name = "session_id", length = 36)
    private String sessionId; // UUID string

    @Column(name = "user_id", nullable = false)
    private Long userId; // server-assigned numeric id for this anonymous user

    @Column(name = "ip", length = 64)
    private String ip;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_seen", nullable = false)
    private LocalDateTime lastSeen;

    public static GradSession create(String ip, long userId) {
        GradSession s = new GradSession();
        s.sessionId = UUID.randomUUID().toString();
        s.userId = userId;
        s.ip = ip;
        s.createdAt = LocalDateTime.now();
        s.lastSeen = s.createdAt;
        return s;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getLastSeen() { return lastSeen; }
    public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }
}
