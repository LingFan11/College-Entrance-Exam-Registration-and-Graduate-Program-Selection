package com.lingfan.xspp.gaokao.domain.repo;

import com.lingfan.xspp.gaokao.domain.entity.RankMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RankMappingRepository extends JpaRepository<RankMapping, Long> {
    Optional<RankMapping> findTopByProvinceAndSubjectTypeAndYearAndScoreOrderByIdDesc(String province, String subjectType, Integer year, Integer score);

    // nearest neighbors for score when exact match is unavailable
    Optional<RankMapping> findTopByProvinceAndSubjectTypeAndYearAndScoreLessThanEqualOrderByScoreDesc(String province, String subjectType, Integer year, Integer score);
    Optional<RankMapping> findTopByProvinceAndSubjectTypeAndYearAndScoreGreaterThanEqualOrderByScoreAsc(String province, String subjectType, Integer year, Integer score);
}
