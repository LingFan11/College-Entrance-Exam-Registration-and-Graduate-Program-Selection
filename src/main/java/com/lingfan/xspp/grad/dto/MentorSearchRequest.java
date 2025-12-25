package com.lingfan.xspp.grad.dto;

import jakarta.validation.constraints.Min;

public class MentorSearchRequest {
    private Long universityId;
    private Long directionId;
    private String title;
    private String keyword;
    @Min(0)
    private int page = 0;
    @Min(1)
    private int size = 20;

    public Long getUniversityId() { return universityId; }
    public void setUniversityId(Long universityId) { this.universityId = universityId; }
    public Long getDirectionId() { return directionId; }
    public void setDirectionId(Long directionId) { this.directionId = directionId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
