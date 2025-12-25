package com.lingfan.xspp.gaokao.domain.repo;

import com.lingfan.xspp.gaokao.domain.entity.UniversityMajorPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UniversityMajorPlanRepository extends JpaRepository<UniversityMajorPlan, Long> {
    List<UniversityMajorPlan> findByProvinceAndSubjectTypeAndYear(String province, String subjectType, Integer year);
}
