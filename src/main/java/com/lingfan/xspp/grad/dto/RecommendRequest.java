package com.lingfan.xspp.grad.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RecommendRequest {
    @NotNull
    private Long userId;
    @Min(0)
    private int page = 0;
    @Min(1)
    private int size = 20;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
