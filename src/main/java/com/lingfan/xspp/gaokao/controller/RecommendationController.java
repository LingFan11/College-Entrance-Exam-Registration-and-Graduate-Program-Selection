package com.lingfan.xspp.gaokao.controller;

import com.lingfan.xspp.common.ApiResponse;
import com.lingfan.xspp.gaokao.dto.RecommendRequest;
import com.lingfan.xspp.gaokao.dto.RecommendResponse;
import com.lingfan.xspp.gaokao.service.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/gaokao")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/recommend")
    public ApiResponse<RecommendResponse> recommend(@Valid @RequestBody RecommendRequest request) {
        String traceId = UUID.randomUUID().toString();
        RecommendResponse data = recommendationService.recommend(request);
        return ApiResponse.ok(data, traceId);
    }

    @GetMapping("/example")
    public ApiResponse<RecommendRequest> example() {
        String traceId = UUID.randomUUID().toString();
        RecommendRequest req = new RecommendRequest();
        req.setProvince("江苏");
        req.setSubjectType("物理"); // 使用中文与字典保持一致
        req.setScore(605); // 在 rank_band [600,609] 范围内
        req.setRank(5000);
        req.setYear(2024);
        req.setRisk("balanced");
        return ApiResponse.ok(req, traceId);
    }
}
