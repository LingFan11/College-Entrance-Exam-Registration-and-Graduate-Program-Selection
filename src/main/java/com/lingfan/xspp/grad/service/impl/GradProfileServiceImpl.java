package com.lingfan.xspp.grad.service.impl;

import com.lingfan.xspp.grad.entity.GradStudentProfile;
import com.lingfan.xspp.grad.repository.GradStudentProfileRepository;
import com.lingfan.xspp.grad.service.GradProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class GradProfileServiceImpl implements GradProfileService {
    private final GradStudentProfileRepository repo;

    public GradProfileServiceImpl(GradStudentProfileRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional
    public GradStudentProfile saveOrUpdate(GradStudentProfile profile) {
        // upsert by userId
        Optional<GradStudentProfile> existing = repo.findByUserId(profile.getUserId());
        if (existing.isPresent()) {
            GradStudentProfile p = existing.get();
            p.setCurrentUniversity(profile.getCurrentUniversity());
            p.setCurrentMajor(profile.getCurrentMajor());
            p.setGpa(profile.getGpa());
            p.setEnglishScore(profile.getEnglishScore());
            p.setEnglishType(profile.getEnglishType());
            p.setCompetitions(profile.getCompetitions());
            p.setResearchExp(profile.getResearchExp());
            p.setPreferredDirections(profile.getPreferredDirections());
            p.setRegionPref(profile.getRegionPref());
            p.setStylePref(profile.getStylePref());
            p.setTargetTier(profile.getTargetTier());
            p.setTargetUniversities(profile.getTargetUniversities());
            p.setTargetDirectionsTopn(profile.getTargetDirectionsTopn());
            // 考研初试成绩字段 (Requirements 1.3, 1.4)
            p.setExamTotalScore(profile.getExamTotalScore());
            p.setPoliticsScore(profile.getPoliticsScore());
            p.setEnglishExamScore(profile.getEnglishExamScore());
            p.setMathScore(profile.getMathScore());
            p.setProfessionalScore(profile.getProfessionalScore());
            p.setTargetInstitutionGroupId(profile.getTargetInstitutionGroupId());
            p.setUpdatedAt(Instant.now());
            return repo.save(p);
        }
        profile.setUpdatedAt(Instant.now());
        return repo.save(profile);
    }

    @Override
    public Optional<GradStudentProfile> getByUserId(Long userId) {
        return repo.findByUserId(userId);
    }
}
