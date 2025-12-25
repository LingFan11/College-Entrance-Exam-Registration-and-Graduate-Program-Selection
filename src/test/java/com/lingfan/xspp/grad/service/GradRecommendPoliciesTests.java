package com.lingfan.xspp.grad.service;

import com.lingfan.xspp.grad.config.GradRecommendProperties;
import com.lingfan.xspp.grad.entity.*;
import com.lingfan.xspp.grad.service.policy.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class GradRecommendPoliciesTests {

    private PolicyContext baseContext(boolean noPref, Map<Long, Double> topn, List<Long> mentorDirs,
                                      Map<Long, Long> dirParent, Set<String> tags,
                                      GradMentorRequirement req, GradMentorQuota quota,
                                      Map<Long, String> dirDomain, List<GradMajorDomainMap> majorDomainMaps,
                                      String major, Double gpa, String englishType, Integer englishScore,
                                      GradRecommendProperties props) {
        GradStudentProfile profile = new GradStudentProfile();
        profile.setCurrentMajor(major);
        profile.setGpa(gpa);
        profile.setEnglishType(englishType);
        profile.setEnglishScore(englishScore);
        GradMentor mentor = new GradMentor();
        mentor.setId(301L);
        return new PolicyContext(profile, mentor, req,
                mentorDirs, topn, dirParent, tags, quota, props,
                noPref, dirDomain, majorDomainMaps);
    }

    @Test
    void thresholdPolicy_onlyGpa_required() {
        GradMentorRequirement req = new GradMentorRequirement();
        req.setMinGpa(3.5);
        GradRecommendProperties props = new GradRecommendProperties();
        PolicyContext ctx = baseContext(true, Map.of(), List.of(), Map.of(), Set.of(), req, null,
                Map.of(), List.of(), "软件工程", 3.6, null, null, props);
        ScoreAccumulator acc = new ScoreAccumulator(0.0);
        new ThresholdPolicy().apply(ctx, acc);
        Assertions.assertEquals("fit", acc.getStatus());
        // not adding reasons
        Assertions.assertTrue(acc.getReasons().isEmpty());
    }

    @Test
    void thresholdPolicy_english_type_and_score() {
        GradMentorRequirement req = new GradMentorRequirement();
        req.setEnglishType("CET6");
        req.setMinEnglish(500);
        GradRecommendProperties props = new GradRecommendProperties();
        PolicyContext ctx = baseContext(true, Map.of(), List.of(), Map.of(), Set.of(), req, null,
                Map.of(), List.of(), "软件工程", 3.6, "CET6", 480, props);
        ScoreAccumulator acc = new ScoreAccumulator(0.0);
        new ThresholdPolicy().apply(ctx, acc);
        Assertions.assertEquals("unfit", acc.getStatus());
        Assertions.assertTrue(acc.getReasons().stream().anyMatch(s -> s.contains("不足")));
    }

    @Test
    void directionProximity_exact_match_scores_high() {
        GradRecommendProperties props = new GradRecommendProperties();
        Map<Long, Double> topn = new LinkedHashMap<>();
        topn.put(102L, 1.0);
        List<Long> mentorDirs = List.of(102L);
        PolicyContext ctx = baseContext(false, topn, mentorDirs, Map.of(), Set.of(), null, null,
                Map.of(), List.of(), "软件工程", 3.6, "CET6", 570, props);
        ScoreAccumulator acc = new ScoreAccumulator(0.0);
        new DirectionProximityPolicy().apply(ctx, acc);
        Assertions.assertTrue(acc.getScore() >= 90.0);
        Assertions.assertEquals("fit", acc.getStatus());
    }

    @Test
    void quotaPenalty_applies_and_marks_borderline() {
        GradMentorQuota q = new GradMentorQuota();
        q.setTotalSlots(1);
        q.setFilledSlots(1);
        GradRecommendProperties props = new GradRecommendProperties();
        PolicyContext ctx = baseContext(true, Map.of(), List.of(), Map.of(), Set.of(), null, q,
                Map.of(), List.of(), "软件工程", 3.6, "CET6", 570, props);
        ScoreAccumulator acc = new ScoreAccumulator(60.0);
        new QuotaPenaltyPolicy().apply(ctx, acc);
        Assertions.assertTrue(acc.getScore() <= 50.0);
        Assertions.assertEquals("borderline", acc.getStatus());
    }

    @Test
    void domainPenalty_cross_penalized() {
        GradRecommendProperties props = new GradRecommendProperties();
        props.setDomainMismatchPenalty(30);
        // major mapped to LIT
        GradMajorDomainMap m1 = new GradMajorDomainMap(); m1.setDomainCode("LIT"); m1.setKeyword("汉语言");
        // mentor direction mapped to CS
        Map<Long, String> dirDomain = Map.of(101L, "CS");
        PolicyContext ctx = baseContext(false, Map.of(102L, 1.0), List.of(101L), Map.of(102L, 101L), Set.of(), null, null,
                dirDomain, List.of(m1), "汉语言文学", 3.6, "CET6", 570, props);
        ScoreAccumulator acc = new ScoreAccumulator(60.0);
        new DomainPenaltyPolicy().apply(ctx, acc);
        Assertions.assertTrue(acc.getScore() <= 30.0);
        Assertions.assertTrue(acc.getReasons().contains("专业方向不匹配"));
    }

    @Test
    void missingInfoPenalty_applies() {
        GradRecommendProperties props = new GradRecommendProperties();
        props.setMissingInfoPenalty(5);
        PolicyContext ctx = baseContext(true, Map.of(), List.of(), Map.of(), Set.of(), null, null,
                Map.of(), List.of(), "", null, "", null, props);
        ScoreAccumulator acc = new ScoreAccumulator(20.0);
        new MissingInfoPenaltyPolicy().apply(ctx, acc);
        // 3 fields missing → -15
        Assertions.assertEquals(5.0, acc.getScore(), 0.001);
        Assertions.assertTrue(acc.getReasons().size() >= 3);
    }
}
