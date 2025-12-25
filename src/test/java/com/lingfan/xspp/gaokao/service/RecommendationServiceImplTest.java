package com.lingfan.xspp.gaokao.service;

import com.lingfan.xspp.gaokao.domain.entity.Major;
import com.lingfan.xspp.gaokao.domain.entity.University;
import com.lingfan.xspp.gaokao.domain.entity.UniversityMajorPlan;
import com.lingfan.xspp.gaokao.domain.entity.UniversityMajorStat;
import com.lingfan.xspp.gaokao.domain.repo.MajorRepository;
import com.lingfan.xspp.gaokao.domain.repo.UniversityMajorPlanRepository;
import com.lingfan.xspp.gaokao.domain.repo.UniversityMajorStatRepository;
import com.lingfan.xspp.gaokao.domain.repo.UniversityRepository;
import com.lingfan.xspp.gaokao.config.RecommendProperties;
import com.lingfan.xspp.gaokao.dto.RecommendRequest;
import com.lingfan.xspp.gaokao.dto.RecommendResponse;
import com.lingfan.xspp.gaokao.exception.BizException;
import com.lingfan.xspp.gaokao.service.impl.RecommendationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.lingfan.xspp.gaokao.dto.RankResolution;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

public class RecommendationServiceImplTest {

    private UniversityMajorPlanRepository planRepo;
    private UniversityMajorStatRepository statRepo;
    private UniversityRepository universityRepo;
    private MajorRepository majorRepo;
    private RankingService rankingService;

    private RecommendationService service;

    @BeforeEach
    void setUp() {
        planRepo = Mockito.mock(UniversityMajorPlanRepository.class);
        statRepo = Mockito.mock(UniversityMajorStatRepository.class);
        universityRepo = Mockito.mock(UniversityRepository.class);
        majorRepo = Mockito.mock(MajorRepository.class);
        rankingService = Mockito.mock(RankingService.class);
        RecommendProperties props = new RecommendProperties();
        service = new RecommendationServiceImpl(planRepo, statRepo, universityRepo, majorRepo, rankingService, props);
    }

    @Test
    void recommend_firstSubjectMismatch_shouldUnfit() {
        // given
        RecommendRequest req = new RecommendRequest();
        req.setProvince("江苏");
        req.setSubjectType("物理");
        req.setFirstSubject("历史"); // 与 plan.subject_type 不一致，期望被软过滤掉
        req.setRank(5000);
        req.setYear(2024);

        RankResolution rr = new RankResolution(5000, "input_rank");
        Mockito.when(rankingService.resolveRankWithMeta(any(), any(), any(), any(), any())).thenReturn(rr);

        UniversityMajorPlan p = plan(1L,1L,"江苏","物理","本科批",6200,2024,60);
        Mockito.when(planRepo.findByProvinceAndSubjectTypeAndYear("江苏","物理",2024))
                .thenReturn(List.of(p));
        UniversityMajorStat s = stat(1L,1L,"江苏","物理",2024, null, 3500);
        Mockito.when(statRepo.findByProvinceAndSubjectTypeAndYear("江苏","物理",2024))
                .thenReturn(List.of(s));
        University u = new University(); u.setId(1L); u.setName("东南大学"); u.setCity("南京"); u.setTier("双一流");
        Mockito.when(universityRepo.findAll()).thenReturn(List.of(u));
        Major m = new Major(); m.setId(1L); m.setName("计算机科学与技术"); m.setCategory("工学");
        Mockito.when(majorRepo.findAll()).thenReturn(List.of(m));

        Assertions.assertThrows(BizException.class, () -> service.recommend(req));
    }

    @Test
    void recommend_normal_shouldReturnBuckets() {
        // given
        RecommendRequest req = new RecommendRequest();
        req.setProvince("江苏");
        req.setSubjectType("物理");
        req.setRank(5000);
        req.setYear(2024);
        RecommendRequest.Preferences prefs = new RecommendRequest.Preferences();
        req.setPreferences(prefs);

        // mock ranking
        RankResolution rr = new RankResolution(5000, "input_rank");
        Mockito.when(rankingService.resolveRankWithMeta(any(), any(), any(), any(), any())).thenReturn(rr);

        // mock data: 1 university, 3 majors
        University u = new University();
        u.setId(1L); u.setName("东南大学"); u.setCity("南京"); u.setTier("双一流");
        Major m1 = new Major(); m1.setId(1L); m1.setName("计算机科学与技术"); m1.setCategory("工学");
        Major m2 = new Major(); m2.setId(2L); m2.setName("软件工程"); m2.setCategory("工学");
        Major m3 = new Major(); m3.setId(3L); m3.setName("土木工程"); m3.setCategory("工学");

        Mockito.when(universityRepo.findAll()).thenReturn(List.of(u));
        Mockito.when(majorRepo.findAll()).thenReturn(List.of(m1, m2, m3));

        UniversityMajorPlan p1 = plan(1L,1L,"江苏","物理","本科批",6200,2024,60);
        UniversityMajorPlan p2 = plan(1L,2L,"江苏","物理","本科批",6200,2024,40);
        UniversityMajorPlan p3 = plan(1L,3L,"江苏","物理","本科批",6200,2024,50);
        Mockito.when(planRepo.findByProvinceAndSubjectTypeAndYear("江苏","物理",2024))
                .thenReturn(List.of(p1,p2,p3));

        UniversityMajorStat s1 = stat(1L,1L,"江苏","物理",2024, null, 3500);
        UniversityMajorStat s2 = stat(1L,2L,"江苏","物理",2024, null, 5000);
        UniversityMajorStat s3 = stat(1L,3L,"江苏","物理",2024, null, 8000);
        Mockito.when(statRepo.findByProvinceAndSubjectTypeAndYear("江苏","物理",2024))
                .thenReturn(List.of(s1,s2,s3));

        // when
        RecommendResponse resp = service.recommend(req);

        // then
        // 新分桶逻辑：delta = candidateRank(5000) - lastYearMinRank
        // 计算机科学与技术: delta = 5000 - 3500 = 1500 > 50 → rush（考生位次差于去年最低，需要冲）
        // 软件工程: delta = 5000 - 5000 = 0, |0| <= 50 → stable（接近去年最低位次）
        // 土木工程: delta = 5000 - 8000 = -3000 < -50 → safe（考生位次远好于去年最低，很保险）
        Assertions.assertNotNull(resp);
        Assertions.assertNotNull(resp.getBuckets());
        Assertions.assertTrue(resp.getBuckets().getStable().stream().anyMatch(it -> "软件工程".equals(it.getMajorName())));
        Assertions.assertTrue(resp.getBuckets().getRush().stream().anyMatch(it -> "计算机科学与技术".equals(it.getMajorName())));
        Assertions.assertTrue(resp.getBuckets().getSafe().stream().anyMatch(it -> "土木工程".equals(it.getMajorName())));
    }

    @Test
    void recommend_tuitionTooLow_shouldUnfit() {
        RecommendRequest req = new RecommendRequest();
        req.setProvince("江苏");
        req.setSubjectType("物理");
        req.setRank(5000);
        req.setYear(2024);
        RecommendRequest.Preferences prefs = new RecommendRequest.Preferences();
        prefs.setTuitionMax(1000);
        req.setPreferences(prefs);

        RankResolution rr = new RankResolution(5000, "input_rank");
        Mockito.when(rankingService.resolveRankWithMeta(any(), any(), any(), any(), any())).thenReturn(rr);
        // plans exist but all filtered out by tuition
        UniversityMajorPlan p = plan(1L,1L,"江苏","物理","本科批",6200,2024,60);
        Mockito.when(planRepo.findByProvinceAndSubjectTypeAndYear("江苏","物理",2024))
                .thenReturn(List.of(p));
        UniversityMajorStat s = stat(1L,1L,"江苏","物理",2024, null, 3500);
        Mockito.when(statRepo.findByProvinceAndSubjectTypeAndYear("江苏","物理",2024))
                .thenReturn(List.of(s));
        University u = new University(); u.setId(1L); u.setName("东南大学"); u.setCity("南京"); u.setTier("双一流");
        Mockito.when(universityRepo.findAll()).thenReturn(List.of(u));
        Major m = new Major(); m.setId(1L); m.setName("计算机科学与技术"); m.setCategory("工学");
        Mockito.when(majorRepo.findAll()).thenReturn(List.of(m));

        // then
        Assertions.assertThrows(BizException.class, () -> service.recommend(req));
    }

    private UniversityMajorPlan plan(Long uid, Long mid, String province, String subject, String batch, int tuition, int year, int count) {
        UniversityMajorPlan p = new UniversityMajorPlan();
        p.setUniversityId(uid); p.setMajorId(mid); p.setProvince(province); p.setSubjectType(subject);
        p.setBatch(batch); p.setTuition(tuition); p.setYear(year); p.setPlanCount(count);
        return p;
    }

    private UniversityMajorStat stat(Long uid, Long mid, String province, String subject, int year, Integer minScore, Integer minRank) {
        UniversityMajorStat s = new UniversityMajorStat();
        s.setUniversityId(uid); s.setMajorId(mid); s.setProvince(province); s.setSubjectType(subject);
        s.setYear(year); s.setMinScore(minScore); s.setMinRank(minRank);
        return s;
    }
}
