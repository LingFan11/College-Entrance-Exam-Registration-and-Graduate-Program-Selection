package com.lingfan.xspp.gaokao.domain.repo;

import com.lingfan.xspp.gaokao.domain.entity.RankBand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RankBandRepository extends JpaRepository<RankBand, Long> {
    Optional<RankBand> findTopByProvinceAndSubjectTypeAndYearAndScoreMinLessThanEqualAndScoreMaxGreaterThanEqualOrderByIdDesc(
            String province, String subjectType, Integer year, Integer scoreMinLE, Integer scoreMaxGE);
}
