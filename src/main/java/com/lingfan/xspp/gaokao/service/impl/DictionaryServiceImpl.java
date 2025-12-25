package com.lingfan.xspp.gaokao.service.impl;

import com.lingfan.xspp.gaokao.domain.entity.Major;
import com.lingfan.xspp.gaokao.domain.entity.University;
import com.lingfan.xspp.gaokao.domain.entity.UniversityMajorPlan;
import com.lingfan.xspp.gaokao.domain.repo.MajorRepository;
import com.lingfan.xspp.gaokao.domain.repo.UniversityMajorPlanRepository;
import com.lingfan.xspp.gaokao.domain.repo.UniversityRepository;
import com.lingfan.xspp.gaokao.dto.DictionaryResponse;
import com.lingfan.xspp.gaokao.service.DictionaryService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DictionaryServiceImpl implements DictionaryService {

    private final UniversityRepository universityRepository;
    private final MajorRepository majorRepository;
    private final UniversityMajorPlanRepository planRepository;

    public DictionaryServiceImpl(UniversityRepository universityRepository,
                                 MajorRepository majorRepository,
                                 UniversityMajorPlanRepository planRepository) {
        this.universityRepository = universityRepository;
        this.majorRepository = majorRepository;
        this.planRepository = planRepository;
    }

    @Override
    public DictionaryResponse getDictionaries() {
        List<University> universities = universityRepository.findAll();
        List<Major> majors = majorRepository.findAll();
        List<UniversityMajorPlan> plans = planRepository.findAll();

        Set<String> provinces = plans.stream().map(UniversityMajorPlan::getProvince).collect(Collectors.toSet());
        Set<String> subjectTypes = plans.stream().map(UniversityMajorPlan::getSubjectType).collect(Collectors.toSet());
        Set<String> batches = plans.stream().map(UniversityMajorPlan::getBatch).collect(Collectors.toSet());
        Set<String> tiers = universities.stream().map(University::getTier).collect(Collectors.toSet());
        Set<String> cities = universities.stream().map(University::getCity).collect(Collectors.toSet());
        Set<String> majorCategories = majors.stream().map(Major::getCategory).collect(Collectors.toSet());

        DictionaryResponse resp = new DictionaryResponse();
        resp.setProvinces(provinces.stream().sorted().toList());
        resp.setSubjectTypes(subjectTypes.stream().sorted().toList());
        // 静态下发：首选/次选科目字典（后续可改为DB配置）
        resp.setFirstSubjects(Arrays.asList("物理", "历史"));
        resp.setSecondSubjects(Arrays.asList("化学", "生物", "政治", "地理"));
        resp.setBatches(batches.stream().sorted().toList());
        resp.setTiers(tiers.stream().sorted().toList());
        resp.setCities(cities.stream().sorted().toList());
        resp.setMajorCategories(majorCategories.stream().sorted().toList());
        return resp;
    }
}
