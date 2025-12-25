package com.lingfan.xspp.grad.dto;

public class SessionInitResponse {
    private Long userId;
    private String sessionId;

    public SessionInitResponse() {}
    public SessionInitResponse(Long userId, String sessionId) {
        this.userId = userId; this.sessionId = sessionId;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}
