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
import com.lingfan.xspp.gaokao.dto.RecommendationItem;
import com.lingfan.xspp.gaokao.service.impl.RecommendationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Comparator;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;

public class RecommendationServiceFitScoreTest {

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
    void fitScore_shouldBeDynamic_andReflectProximityAndPrefs() {
        // given candidate rank close to 5000
        Mockito.when(rankingService.resolveRank(any(), any(), any(), any(), any())).thenReturn(5000);

        University u = new University();
        u.setId(1L); u.setName("东南大学"); u.setCity("南京"); u.setTier("双一流");
        Mockito.when(universityRepo.findAll()).thenReturn(List.of(u));

        Major m1 = new Major(); m1.setId(1L); m1.setName("软件工程"); m1.setCategory("工学"); // minRank=5000 → 接近
        Major m2 = new Major(); m2.setId(2L); m2.setName("土木工程"); m2.setCategory("工学");   // minRank=8000 → 远
        Mockito.when(majorRepo.findAll()).thenReturn(List.of(m1, m2));

        UniversityMajorPlan p1 = plan(1L,1L,"江苏","physics","本科批",6200,2024,40);
        UniversityMajorPlan p2 = plan(1L,2L,"江苏","physics","本科批",6200,2024,50);
        Mockito.when(planRepo.findByProvinceAndSubjectTypeAndYear("江苏","物理",2024))
                .thenReturn(List.of(p1,p2));

        UniversityMajorStat s1 = stat(1L,1L,"江苏","物理",2024, null, 5000);
        UniversityMajorStat s2 = stat(1L,2L,"江苏","物理",2024, null, 8000);
        Mockito.when(statRepo.findByProvinceAndSubjectTypeAndYear("江苏","物理",2024))
                .thenReturn(List.of(s1,s2));

        RecommendRequest req = new RecommendRequest();
        req.setProvince("江苏"); req.setSubjectType("物理"); req.setYear(2024); req.setRank(5000);
        RecommendRequest.Preferences prefs = new RecommendRequest.Preferences();
        prefs.setCities(List.of("南京")); // 命中
        prefs.setTiers(List.of("双一流")); // 命中
        // 不设置 majors 关键词，避免过滤掉非关键词专业，确保返回包含两个专业
        req.setPreferences(prefs);

        // when
        RecommendResponse resp = service.recommend(req);
        List<RecommendationItem> all = List.of(
                resp.getBuckets().getRush().stream(),
                resp.getBuckets().getStable().stream(),
                resp.getBuckets().getSafe().stream()
        ).stream().flatMap(s -> s).sorted(Comparator.comparing(RecommendationItem::getMajorName)).toList();

        // then - 两个专业得分应该不同，且软件工程（更接近+命中关键词）分更高
        Assertions.assertEquals(2, all.size());
        RecommendationItem item1 = all.get(0); // 软件工程 or 土木工程，排序后取名断言
        RecommendationItem item2 = all.get(1);
        RecommendationItem soft = item1.getMajorName().contains("软件") ? item1 : item2;
        RecommendationItem civil = item1.getMajorName().contains("土木") ? item1 : item2;
        Assertions.assertNotEquals(soft.getFitScore(), civil.getFitScore(), "不同专业匹配度应不同");
        Assertions.assertTrue(soft.getFitScore() > civil.getFitScore(), "软件工程应比分更高（更接近 + 偏好命中）");
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
