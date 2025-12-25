package com.lingfan.xspp.gaokao.domain.repo;

import com.lingfan.xspp.gaokao.domain.entity.UniversityMajorStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UniversityMajorStatRepository extends JpaRepository<UniversityMajorStat, Long> {
    List<UniversityMajorStat> findByProvinceAndSubjectTypeAndYear(String province, String subjectType, Integer year);
}
