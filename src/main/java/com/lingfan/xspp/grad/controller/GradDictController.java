package com.lingfan.xspp.grad.controller;

import com.lingfan.xspp.common.ApiResponse;
import com.lingfan.xspp.grad.dto.InstitutionGroupDTO;
import com.lingfan.xspp.grad.entity.GradResearchDirection;
import com.lingfan.xspp.grad.repository.GradResearchDirectionRepository;
import com.lingfan.xspp.grad.service.DictCacheService;
import com.lingfan.xspp.grad.service.GradInstitutionGroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/grad/dicts")
public class GradDictController {
    private final GradResearchDirectionRepository dirRepo;
    private final DictCacheService dictCacheService;
    private final GradInstitutionGroupService institutionGroupService;

    public GradDictController(GradResearchDirectionRepository dirRepo, 
                              DictCacheService dictCacheService,
                              GradInstitutionGroupService institutionGroupService) {
        this.dirRepo = dirRepo;
        this.dictCacheService = dictCacheService;
        this.institutionGroupService = institutionGroupService;
    }

    @GetMapping("/directions")
    public ApiResponse<List<GradResearchDirection>> listDirections(@RequestParam(value = "parentId", required = false) Long parentId) {
        String traceId = UUID.randomUUID().toString();
        if (parentId == null) {
            return ApiResponse.ok(dirRepo.findAll(), traceId);
        }
        // simple filter in memory; repository could be extended with a query if needed
        List<GradResearchDirection> all = dirRepo.findAll();
        List<GradResearchDirection> filtered = all.stream().filter(d -> {
            if (parentId == null) return true;
            Long p = d.getParentId();
            return (p == null && parentId == 0) || (p != null && p.equals(parentId));
        }).toList();
        return ApiResponse.ok(filtered, traceId);
    }

    @PostMapping("/refresh")
    public ApiResponse<Void> refreshCache() {
        String traceId = UUID.randomUUID().toString();
        dictCacheService.refresh();
        return ApiResponse.ok(null, traceId);
    }

    /**
     * 获取机构分组字典
     * Requirements: 4.1, 5.3
     * 
     * @param institutionCode 机构代码，如 'CAS' 表示中科院
     * @return 分组列表，按 minTotalScore 降序排列
     */
    @GetMapping("/institution-groups")
    public ApiResponse<List<InstitutionGroupDTO>> listInstitutionGroups(
            @RequestParam("institutionCode") String institutionCode) {
        String traceId = UUID.randomUUID().toString();
        List<InstitutionGroupDTO> groups = institutionGroupService.getGroupsByInstitution(institutionCode);
        return ApiResponse.ok(groups, traceId);
    }
}
