package com.lingfan.xspp.grad.service;

import com.lingfan.xspp.grad.dto.RecommendResultDTO;

import java.util.List;

public interface GradRecommendService {
    List<RecommendResultDTO> recommend(Long userId, int page, int size);
}
