package com.lingfan.xspp.grad.dto;

import java.util.List;

public class RecommendResultDTO {
    private Long mentorId;
    private String mentorName;
    private Double score;
    private String status; // fit|borderline|unfit
    private List<String> reasons;

    public RecommendResultDTO() {}
    public RecommendResultDTO(Long mentorId, String mentorName, Double score, String status, List<String> reasons) {
        this.mentorId = mentorId; this.mentorName = mentorName; this.score = score; this.status = status; this.reasons = reasons;
    }

    public Long getMentorId() { return mentorId; }
    public void setMentorId(Long mentorId) { this.mentorId = mentorId; }
    public String getMentorName() { return mentorName; }
    public void setMentorName(String mentorName) { this.mentorName = mentorName; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }
}
