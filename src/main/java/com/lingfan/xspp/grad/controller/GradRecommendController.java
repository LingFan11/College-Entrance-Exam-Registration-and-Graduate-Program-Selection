package com.lingfan.xspp.grad.controller;

import com.lingfan.xspp.common.ApiResponse;
import com.lingfan.xspp.grad.dto.RecommendResultDTO;
import com.lingfan.xspp.grad.dto.RecommendRequest;
import com.lingfan.xspp.grad.service.GradRecommendService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/grad/recommend")
@Validated
public class GradRecommendController {
    private final GradRecommendService recommendService;

    public GradRecommendController(GradRecommendService recommendService) {
        this.recommendService = recommendService;
    }

    // Minimal: trigger + return results together via POST; accept userId as request param
    @PostMapping
    public ApiResponse<List<RecommendResultDTO>> triggerAndList(
            @RequestParam("userId") @NotNull Long userId,
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) int size
    ) {
        String traceId = UUID.randomUUID().toString();
        List<RecommendResultDTO> list = recommendService.recommend(userId, page, size);
        return ApiResponse.ok(list, traceId);
    }

    // Optional GET for convenience
    @GetMapping
    public ApiResponse<List<RecommendResultDTO>> list(
            @RequestParam("userId") @NotNull Long userId,
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) int size
    ) {
        String traceId = UUID.randomUUID().toString();
        List<RecommendResultDTO> list = recommendService.recommend(userId, page, size);
        return ApiResponse.ok(list, traceId);
    }

    // Body-style POST endpoint for frontend integration
    @PostMapping(path = "/compute", consumes = "application/json")
    public ApiResponse<List<RecommendResultDTO>> compute(@Valid @RequestBody RecommendRequest req) {
        String traceId = UUID.randomUUID().toString();
        List<RecommendResultDTO> list = recommendService.recommend(req.getUserId(), req.getPage(), req.getSize());
        return ApiResponse.ok(list, traceId);
    }
}
