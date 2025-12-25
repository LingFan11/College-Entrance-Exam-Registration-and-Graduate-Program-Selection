package com.lingfan.xspp.grad.controller;

import com.lingfan.xspp.common.ApiResponse;
import com.lingfan.xspp.grad.dto.GroupMatchResponse;
import com.lingfan.xspp.grad.service.GradInstitutionGroupService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 机构分组控制器
 * Requirements: 4.2, 4.3
 */
@RestController
@RequestMapping("/api/grad/institution-groups")
public class GradInstitutionGroupController {

    private final GradInstitutionGroupService institutionGroupService;

    public GradInstitutionGroupController(GradInstitutionGroupService institutionGroupService) {
        this.institutionGroupService = institutionGroupService;
    }

    /**
     * 根据学生总分匹配最佳机构分组
     * Requirements: 4.2, 4.3
     * 
     * @param institutionCode 机构代码，如 'CAS' 表示中科院
     * @param totalScore 学生初试总分
     * @return 匹配结果，包含最佳匹配分组和备选分组
     */
    @GetMapping("/match")
    public ApiResponse<GroupMatchResponse> matchGroup(
            @RequestParam("institutionCode") String institutionCode,
            @RequestParam(value = "totalScore", required = false) Integer totalScore) {
        String traceId = UUID.randomUUID().toString();
        GroupMatchResponse response = institutionGroupService.matchGroup(institutionCode, totalScore);
        return ApiResponse.ok(response, traceId);
    }
}
