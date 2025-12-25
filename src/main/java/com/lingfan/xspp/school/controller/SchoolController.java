package com.lingfan.xspp.school.controller;

import com.lingfan.xspp.common.ApiResponse;
import com.lingfan.xspp.school.dto.*;
import com.lingfan.xspp.school.service.SchoolService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/school")
@Validated
public class SchoolController {

    private final SchoolService schoolService;

    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    @GetMapping("/list")
    public ApiResponse<Page<SchoolBriefDTO>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "level", required = false) String level,
            @RequestParam(value = "province", required = false) String province,
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        String traceId = UUID.randomUUID().toString();
        Page<SchoolBriefDTO> result = schoolService.search(keyword, level, province, page, size);
        return ApiResponse.ok(result, traceId);
    }

    @GetMapping("/{id}")
    public ApiResponse<SchoolDetailDTO> detail(@PathVariable("id") Long id) {
        String traceId = UUID.randomUUID().toString();
        return schoolService.getById(id)
                .map(dto -> ApiResponse.ok(dto, traceId))
                .orElseGet(() -> ApiResponse.unfit("school_not_found", traceId));
    }

    @GetMapping("/{id}/departments")
    public ApiResponse<java.util.List<DepartmentDTO>> departments(@PathVariable("id") Long id) {
        String traceId = UUID.randomUUID().toString();
        return ApiResponse.ok(schoolService.listDepartments(id), traceId);
    }

    @GetMapping("/{id}/disciplines/strength")
    public ApiResponse<java.util.List<DisciplineStrengthDTO>> strengths(
            @PathVariable("id") Long id,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "topN", required = false) @Min(1) @Max(50) Integer topN,
            @RequestParam(value = "page", required = false) @Min(0) Integer page,
            @RequestParam(value = "size", required = false) @Min(1) @Max(100) Integer size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "grade") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order
    ) {
        String traceId = UUID.randomUUID().toString();
        return ApiResponse.ok(schoolService.listDisciplineStrength(id, year, topN, page, size, sortBy, order), traceId);
    }

    @GetMapping("/{id}/majors")
    public ApiResponse<java.util.List<MajorDTO>> majors(
            @PathVariable("id") Long id,
            @RequestParam(value = "degree", required = false) String degree,
            @RequestParam(value = "disciplineId", required = false) Long disciplineId,
            @RequestParam(value = "page", required = false) @Min(0) Integer page,
            @RequestParam(value = "size", required = false) @Min(1) @Max(100) Integer size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "name") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order
    ) {
        String traceId = UUID.randomUUID().toString();
        return ApiResponse.ok(schoolService.listMajors(id, degree, disciplineId, page, size, sortBy, order), traceId);
    }

    @GetMapping("/{id}/brochures")
    public ApiResponse<java.util.List<BrochureDTO>> brochures(
            @PathVariable("id") Long id,
            @RequestParam(value = "degree", required = false) String degree,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "page", required = false) @Min(0) Integer page,
            @RequestParam(value = "size", required = false) @Min(1) @Max(100) Integer size
    ) {
        String traceId = UUID.randomUUID().toString();
        return ApiResponse.ok(schoolService.listBrochures(id, degree, year, page, size), traceId);
    }

    // ===== Dicts & Tags =====
    @GetMapping("/dicts/levels")
    public ApiResponse<java.util.List<String>> levels() {
        String traceId = UUID.randomUUID().toString();
        return ApiResponse.ok(schoolService.listLevels(), traceId);
    }

    @GetMapping("/dicts/disciplines")
    public ApiResponse<java.util.List<com.lingfan.xspp.school.dto.DisciplineDTO>> disciplines(
            @RequestParam(value = "parentId", required = false) Long parentId
    ) {
        String traceId = UUID.randomUUID().toString();
        return ApiResponse.ok(schoolService.listDisciplines(parentId), traceId);
    }

    @GetMapping("/tags")
    public ApiResponse<java.util.List<String>> tags(
            @RequestParam("type") String type,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        String traceId = UUID.randomUUID().toString();
        return ApiResponse.ok(schoolService.listTags(type, keyword), traceId);
    }
}
