package com.lingfan.xspp.gaokao.service;

import com.lingfan.xspp.gaokao.domain.entity.RankMapping;
import com.lingfan.xspp.gaokao.domain.repo.RankMappingRepository;
import com.lingfan.xspp.gaokao.domain.repo.RankBandRepository;
import com.lingfan.xspp.gaokao.exception.BizException;
import com.lingfan.xspp.gaokao.service.impl.RankingServiceImpl;
import com.lingfan.xspp.gaokao.config.RecommendProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

public class RankingServiceImplTest {

    private RankMappingRepository repo;
    private RankBandRepository bandRepo;
    private RankingService service;

    @BeforeEach
    void setUp() {
        repo = Mockito.mock(RankMappingRepository.class);
        bandRepo = Mockito.mock(RankBandRepository.class);
        RecommendProperties props = new RecommendProperties();
        service = new RankingServiceImpl(repo, bandRepo, props);
    }

    @Test
    void whenBandHit_shouldPreferBandConservativeRankMax() {
        // band hit: score in [600,609] → rank range [3000,4200], prefer conservative rankMax
        com.lingfan.xspp.gaokao.domain.entity.RankBand band = new com.lingfan.xspp.gaokao.domain.entity.RankBand();
        band.setProvince("江苏"); band.setSubjectType("physics"); band.setYear(2024);
        band.setScoreMin(600); band.setScoreMax(609); band.setRankMin(3000); band.setRankMax(4200);
        Mockito.when(bandRepo.findTopByProvinceAndSubjectTypeAndYearAndScoreMinLessThanEqualAndScoreMaxGreaterThanEqualOrderByIdDesc(
                anyString(), anyString(), anyInt(), anyInt(), anyInt()
        )).thenReturn(Optional.of(band));

        int result = service.resolveRank(null, 605, "江苏", "physics", 2024);
        Assertions.assertEquals(4200, result);
    }

    @Test
    void whenRankProvided_shouldReturnDirectly() {
        // rank <= 50 (maskTopRank) 时无需提供 score
        int result = service.resolveRank(30, null, "江苏", "physics", 2024);
        Assertions.assertEquals(30, result);
    }

    @Test
    void whenRankBeyondMaskWithScore_shouldReturnRank() {
        // rank > 50 时需要提供 score，但如果一致性校验通过，返回 rank
        RankMapping m = new RankMapping();
        m.setProvince("江苏"); m.setSubjectType("physics"); m.setYear(2024); m.setScore(600); m.setRankValue(5000);
        Mockito.when(repo.findTopByProvinceAndSubjectTypeAndYearAndScoreOrderByIdDesc(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(Optional.of(m));
        // rank=5000, score=600 映射后也是 5000，一致性通过
        int result = service.resolveRank(5000, 600, "江苏", "physics", 2024);
        Assertions.assertEquals(5000, result);
    }

    @Test
    void whenScoreCanMap_shouldReturnMapped() {
        RankMapping m = new RankMapping();
        m.setProvince("江苏"); m.setSubjectType("physics"); m.setYear(2024); m.setScore(600); m.setRankValue(3000);
        Mockito.when(repo.findTopByProvinceAndSubjectTypeAndYearAndScoreOrderByIdDesc(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(Optional.of(m));
        int result = service.resolveRank(null, 600, "江苏", "physics", 2024);
        Assertions.assertEquals(3000, result);
    }

    @Test
    void whenScoreCannotMap_shouldThrow() {
        Mockito.when(repo.findTopByProvinceAndSubjectTypeAndYearAndScoreOrderByIdDesc(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(BizException.class, () -> service.resolveRank(null, 580, "江苏", "physics", 2024));
    }

    @Test
    void whenExactMissing_useNearestWithinDelta_shouldReturn() {
        // exact miss
        Mockito.when(repo.findTopByProvinceAndSubjectTypeAndYearAndScoreOrderByIdDesc(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(Optional.empty());
        // lower neighbor at 598 -> rank 3200
        RankMapping lower = new RankMapping();
        lower.setProvince("江苏"); lower.setSubjectType("physics"); lower.setYear(2024); lower.setScore(598); lower.setRankValue(3200);
        Mockito.when(repo.findTopByProvinceAndSubjectTypeAndYearAndScoreLessThanEqualOrderByScoreDesc(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(Optional.of(lower));
        // upper neighbor at 602 -> rank 2800
        RankMapping upper = new RankMapping();
        upper.setProvince("江苏"); upper.setSubjectType("physics"); upper.setYear(2024); upper.setScore(602); upper.setRankValue(2800);
        Mockito.when(repo.findTopByProvinceAndSubjectTypeAndYearAndScoreGreaterThanEqualOrderByScoreAsc(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(Optional.of(upper));

        int result = service.resolveRank(null, 600, "江苏", "physics", 2024);
        // nearest diff 2 分（与两侧同距，取更保守=更大的 rank 值 → 3200）
        Assertions.assertEquals(3200, result);
    }

    @Test
    void whenNearestExceedsDelta_shouldThrow() {
        // exact miss
        Mockito.when(repo.findTopByProvinceAndSubjectTypeAndYearAndScoreOrderByIdDesc(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(Optional.empty());
        // neighbors too far (difference 30 > default 20)
        RankMapping lower = new RankMapping(); lower.setScore(570); lower.setRankValue(7000);
        RankMapping upper = new RankMapping(); upper.setScore(630); upper.setRankValue(2000);
        Mockito.when(repo.findTopByProvinceAndSubjectTypeAndYearAndScoreLessThanEqualOrderByScoreDesc(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(Optional.of(lower));
        Mockito.when(repo.findTopByProvinceAndSubjectTypeAndYearAndScoreGreaterThanEqualOrderByScoreAsc(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(Optional.of(upper));

        Assertions.assertThrows(BizException.class, () -> service.resolveRank(null, 600, "江苏", "physics", 2024));
    }
}
