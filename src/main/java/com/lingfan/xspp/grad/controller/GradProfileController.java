package com.lingfan.xspp.grad.controller;

import com.lingfan.xspp.common.ApiResponse;
import com.lingfan.xspp.grad.dto.ProfileSaveRequest;
import com.lingfan.xspp.grad.entity.GradStudentProfile;
import com.lingfan.xspp.grad.service.GradProfileService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/grad/profile")
public class GradProfileController {
    private final GradProfileService profileService;

    public GradProfileController(GradProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public ApiResponse<GradStudentProfile> save(@Valid @RequestBody ProfileSaveRequest req) {
        String traceId = UUID.randomUUID().toString();
        GradStudentProfile profile = new GradStudentProfile();
        profile.setUserId(req.getUserId());
        profile.setCurrentUniversity(req.getCurrentUniversity());
        profile.setCurrentMajor(req.getCurrentMajor());
        profile.setGpa(req.getGpa());
        profile.setEnglishType(req.getEnglishType());
        profile.setEnglishScore(req.getEnglishScore());
        profile.setCompetitions(req.getCompetitions());
        profile.setResearchExp(req.getResearchExp());
        profile.setPreferredDirections(req.getPreferredDirections());
        profile.setRegionPref(req.getRegionPref());
        profile.setStylePref(req.getStylePref());
        profile.setTargetTier(req.getTargetTier());
        profile.setTargetUniversities(req.getTargetUniversities());
        profile.setTargetDirectionsTopn(req.getTargetDirectionsTopn());
        // 考研初试成绩字段 (Requirements 1.3, 1.4)
        profile.setExamTotalScore(req.getExamTotalScore());
        profile.setPoliticsScore(req.getPoliticsScore());
        profile.setEnglishExamScore(req.getEnglishExamScore());
        profile.setMathScore(req.getMathScore());
        profile.setProfessionalScore(req.getProfessionalScore());
        profile.setTargetInstitutionGroupId(req.getTargetInstitutionGroupId());
        GradStudentProfile saved = profileService.saveOrUpdate(profile);
        return ApiResponse.ok(saved, traceId);
    }

    @GetMapping
    public ApiResponse<GradStudentProfile> get(@RequestParam("userId") Long userId) {
        String traceId = UUID.randomUUID().toString();
        Optional<GradStudentProfile> p = profileService.getByUserId(userId);
        return p.map(profile -> ApiResponse.ok(profile, traceId))
                .orElseGet(() -> ApiResponse.unfit("profile_not_found", traceId));
    }
}
