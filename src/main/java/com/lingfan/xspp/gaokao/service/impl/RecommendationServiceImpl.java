package com.lingfan.xspp.gaokao.service.impl;

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
import com.lingfan.xspp.gaokao.exception.BizException;
import com.lingfan.xspp.gaokao.service.RankingService;
import com.lingfan.xspp.gaokao.service.RecommendationService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    private final UniversityMajorPlanRepository planRepo;
    private final UniversityMajorStatRepository statRepo;
    private final UniversityRepository universityRepo;
    private final MajorRepository majorRepo;
    private final RankingService rankingService;
    private final RecommendProperties props;

    public RecommendationServiceImpl(UniversityMajorPlanRepository planRepo,
                                     UniversityMajorStatRepository statRepo,
                                     UniversityRepository universityRepo,
                                     MajorRepository majorRepo,
                                     RankingService rankingService,
                                     RecommendProperties props) {
        this.planRepo = planRepo;
        this.statRepo = statRepo;
        this.universityRepo = universityRepo;
        this.majorRepo = majorRepo;
        this.rankingService = rankingService;
        this.props = props;
    }

    @Override
    public RecommendResponse recommend(RecommendRequest req) {
        // basic validation
        if (req.getProvince() == null || req.getProvince().isBlank()) {
            throw new BizException("参数不合适：province 必填");
        }
        if (req.getSubjectType() == null || req.getSubjectType().isBlank()) {
            throw new BizException("参数不合适：subjectType 必填");
        }
        Integer year = Optional.ofNullable(req.getYear()).orElse(2024);
        var rrMeta = rankingService.resolveRankWithMeta(req.getRank(), req.getScore(), req.getProvince(), req.getSubjectType(), year);
        int rank = rrMeta.getRank();

        // load plans and stats
        List<UniversityMajorPlan> plans = planRepo.findByProvinceAndSubjectTypeAndYear(req.getProvince(), req.getSubjectType(), year);
        if (plans.isEmpty()) {
            throw new BizException("暂无符合条件的专业志愿");
        }
        List<UniversityMajorStat> stats = statRepo.findByProvinceAndSubjectTypeAndYear(req.getProvince(), req.getSubjectType(), year);
        Map<String, UniversityMajorStat> statKeyed = stats.stream().collect(Collectors.toMap(
                s -> s.getUniversityId() + ":" + s.getMajorId(), Function.identity(), (a,b)->a
        ));
        // previous year for trend
        int prevYear = year - 1;
        List<UniversityMajorStat> prevStats = statRepo.findByProvinceAndSubjectTypeAndYear(req.getProvince(), req.getSubjectType(), prevYear);
        Map<String, UniversityMajorStat> prevStatKeyed = prevStats.stream().collect(Collectors.toMap(
                s -> s.getUniversityId() + ":" + s.getMajorId(), Function.identity(), (a,b)->a
        ));

        // preload universities and majors to maps
        Map<Long, University> univMap = universityRepo.findAll().stream().collect(Collectors.toMap(University::getId, Function.identity()));
        Map<Long, Major> majorMap = majorRepo.findAll().stream().collect(Collectors.toMap(Major::getId, Function.identity()));

        // filter by subject requirements (soft for now)
        RecommendRequest.Preferences prefs = req.getPreferences();
        List<RecommendationItem> items = new ArrayList<>();
        for (UniversityMajorPlan p : plans) {
            UniversityMajorStat st = statKeyed.get(p.getUniversityId() + ":" + p.getMajorId());
            if (st == null) continue; // 无统计数据，跳过
            University u = univMap.get(p.getUniversityId());
            Major m = majorMap.get(p.getMajorId());
            // 科目软过滤：若请求携带 firstSubject，则需与 plan.subject_type 一致（物理/历史）
            if (req.getFirstSubject() != null && !req.getFirstSubject().isEmpty()) {
                if (!req.getFirstSubject().equals(p.getSubjectType())) {
                    continue;
                }
            }

            // gating: 分数达线优先（可配置），缺失则按位次线降级判断
            if (props.isUseMinScoreGate()) {
                boolean passed = true;
                Integer minScore = st.getMinScore();
                Integer minRank = st.getMinRank();
                Integer reqScore = req.getScore();
                if (minScore != null && reqScore != null) {
                    if (reqScore < (minScore + props.getMinScoreGrace())) {
                        passed = false;
                    }
                } else if (minRank != null) {
                    // fallback to rank-based gating when score or minScore missing
                    if (rank > (minRank + props.getRankGrace())) {
                        passed = false;
                    }
                }
                if (!passed) {
                    continue; // 未达线，过滤
                }
            }

            // preference filters (tuition max, city, tier, major keywords)
            if (prefs != null) {
                if (prefs.getTuitionMax() != null && p.getTuition() != null && p.getTuition() > prefs.getTuitionMax()) continue;
                if (prefs.getCities() != null && !prefs.getCities().isEmpty() && !prefs.getCities().contains(u.getCity())) continue;
                if (prefs.getTiers() != null && !prefs.getTiers().isEmpty() && !prefs.getTiers().contains(u.getTier())) continue;
                if (prefs.getMajors() != null && !prefs.getMajors().isEmpty()) {
                    boolean match = prefs.getMajors().stream().anyMatch(kw -> m.getName().contains(kw) || m.getCategory().contains(kw));
                    if (!match) continue;
                }
            }

            RecommendationItem it = new RecommendationItem();
            it.setUniversityId(u.getId());
            it.setUniversityName(u.getName());
            it.setCity(u.getCity());
            it.setTier(u.getTier());
            it.setMajorId(m.getId());
            it.setMajorName(m.getName());
            it.setSubjectType(p.getSubjectType());
            it.setBatch(p.getBatch());
            it.setTuition(p.getTuition());
            it.setLastYearMinRank(st.getMinRank());
            // compute trend based on previous year min_rank
            UniversityMajorStat prev = prevStatKeyed.get(p.getUniversityId() + ":" + p.getMajorId());
            String trend = "平";
            if (prev != null && prev.getMinRank() != null && st.getMinRank() != null) {
                if (st.getMinRank() < prev.getMinRank()) trend = "上";      // 更难（名次更靠前）
                else if (st.getMinRank() > prev.getMinRank()) trend = "下"; // 更易（名次更靠后）
                else trend = "平";
            }
            it.setAdmitTrend(trend);
            // expose last year's minimum score (only from previous year data, no fallback to current year)
            Integer lastYearMinScore = (prev != null) ? prev.getMinScore() : null;
            it.setLastYearMinScore(lastYearMinScore);
            // 使用带趋势调整的匹配度算法
            it.setFitScore(calcFitScoreWithTrend(u, m, p, prefs, rank, st.getMinRank(), trend));
            items.add(it);
        }

        if (items.isEmpty()) {
            throw new BizException("暂无符合条件的专业志愿");
        }

        // bucket by risk
        // 根据 risk 参数调整分桶阈值
        String riskLevel = Optional.ofNullable(req.getRisk()).orElse("balanced");
        int rushWindow = props.getRushWindowRank();
        int stableUpper = props.getStableUpper();
        // 根据风险偏好调整阈值
        switch (riskLevel) {
            case "aggressive":
                // 激进：扩大冲的范围，缩小保的范围
                rushWindow = (int) (rushWindow * 1.5);
                stableUpper = (int) (stableUpper * 1.2);
                break;
            case "conservative":
                // 保守：缩小冲的范围，扩大保的范围
                rushWindow = (int) (rushWindow * 0.5);
                stableUpper = (int) (stableUpper * 0.8);
                break;
            default:
                // balanced: 使用默认值
                break;
        }

        List<RecommendationItem> rush = new ArrayList<>();
        List<RecommendationItem> stable = new ArrayList<>();
        List<RecommendationItem> safe = new ArrayList<>();
        for (RecommendationItem it : items) {
            int delta = rank - Optional.ofNullable(it.getLastYearMinRank()).orElse(Integer.MAX_VALUE);
            // 分桶规则（delta = candidateRank - lastYearMinRank）：
            //   delta < 0 表示考生位次优于去年最低位次（更有把握）
            //   delta > 0 表示考生位次劣于去年最低位次（需要冲）
            //
            // 分桶逻辑：
            //   rush（冲）：delta > rushWindow（考生位次比去年最低位次差，需要冲刺）
            //   stable（稳）：|delta| <= rushWindow（接近去年最低位次，比较稳）
            //   safe（保）：delta < -rushWindow（考生位次远好于去年最低位次，很保险）
            if (delta > rushWindow) {
                rush.add(it);
            } else if (delta >= -rushWindow) {
                stable.add(it);
            } else {
                safe.add(it);
            }
        }

        // sort within buckets by fitScore desc
        Comparator<RecommendationItem> byScore = Comparator.comparing(RecommendationItem::getFitScore, Comparator.nullsLast(Comparator.naturalOrder())).reversed();
        rush.sort(byScore);
        stable.sort(byScore);
        safe.sort(byScore);

        int size = Optional.ofNullable(req.getSize()).orElse(20);
        RecommendResponse.Buckets buckets = new RecommendResponse.Buckets();
        buckets.setRush(truncate(rush, size));
        buckets.setStable(truncate(stable, size));
        buckets.setSafe(truncate(safe, size));

        RecommendResponse resp = new RecommendResponse();
        resp.setBuckets(buckets);
        resp.setExplain("基于上一年最低位次与偏好打分（风险偏好：" + riskLevel + "）：rush(Δ>" + rushWindow + ")、stable(|Δ|≤" + rushWindow + ")、safe(Δ<-" + rushWindow + ")，Δ=candidateRank-lastYearMinRank");
        // 填充位次解析的结构化信息
        resp.setResolvedRank(rrMeta.getRank());
        resp.setRankSource(rrMeta.getSource());
        resp.setRankBandMin(rrMeta.getRankBandMin());
        resp.setRankBandMax(rrMeta.getRankBandMax());
        resp.setScoreUsed(rrMeta.getScoreUsed());
        return resp;
    }

    private List<RecommendationItem> truncate(List<RecommendationItem> list, int size) {
        if (list.size() <= size) return list;
        return list.subList(0, size);
    }

    /**
     * 计算匹配度分数（0-100）
     * 
     * 算法组成：
     * 1. 基础分（30分）- 所有志愿的起点
     * 2. 录取概率分（0-35分）- 基于位次差距，使用非线性衰减
     * 3. 偏好匹配分（0-25分）- 专业12 + 城市6 + 层次7
     * 4. 趋势调整分（-5~+5分）- 根据录取趋势调整
     * 5. 性价比分（0-5分）- 学费合理性
     */
    private int calcFitScore(University u,
                             Major m,
                             UniversityMajorPlan p,
                             RecommendRequest.Preferences prefs,
                             int candidateRank,
                             Integer lastYearMinRank) {
        double score = 30.0; // 基础分

        // 1) 录取概率分（0-35分）- 非线性衰减
        score += calcAdmissionProbabilityScore(candidateRank, lastYearMinRank);

        // 2) 偏好匹配分（0-25分）
        score += calcPreferenceScore(u, m, prefs);

        // 3) 趋势调整分（-5~+5分）
        // 注意：这里需要从 item 获取趋势，但当前方法签名没有传入
        // 暂时跳过，后续可以扩展

        // 4) 性价比分（0-5分）
        score += calcValueScore(p, prefs);

        // 限制在 0~100
        return (int) Math.max(0, Math.min(Math.round(score), 100));
    }

    /**
     * 录取概率分（0-35分）
     * 使用 Sigmoid 变体函数实现非线性衰减
     * 
     * delta = candidateRank - lastYearMinRank
     * - delta < 0: 考生位次优于去年最低，录取概率高
     * - delta > 0: 考生位次劣于去年最低，录取概率低
     */
    private double calcAdmissionProbabilityScore(int candidateRank, Integer lastYearMinRank) {
        if (lastYearMinRank == null || lastYearMinRank <= 0) {
            return 17.5; // 无数据时给中间分
        }

        int delta = candidateRank - lastYearMinRank;
        
        // 使用改进的 Sigmoid 函数
        // 当 delta = 0 时，得分约 17.5（中间值）
        // 当 delta < 0（位次更好）时，得分趋近 35
        // 当 delta > 0（位次更差）时，得分趋近 0
        // k 控制衰减速度，这里设为 0.003（每333名差距衰减一半）
        double k = 0.003;
        double sigmoid = 1.0 / (1.0 + Math.exp(k * delta));
        
        return 35.0 * sigmoid;
    }

    /**
     * 偏好匹配分（0-25分）
     * - 专业匹配：0-12分（精确匹配12分，类别匹配8分）
     * - 城市匹配：0-6分
     * - 层次匹配：0-7分（985=7, 211=5, 双一流=4, 其他=0）
     */
    private double calcPreferenceScore(University u, Major m, RecommendRequest.Preferences prefs) {
        if (prefs == null) {
            return 0;
        }

        double score = 0;

        // 专业匹配（0-12分）
        if (prefs.getMajors() != null && !prefs.getMajors().isEmpty()) {
            boolean exactMatch = prefs.getMajors().stream()
                    .anyMatch(kw -> m.getName().contains(kw));
            boolean categoryMatch = prefs.getMajors().stream()
                    .anyMatch(kw -> m.getCategory().contains(kw));
            
            if (exactMatch) {
                score += 12;
            } else if (categoryMatch) {
                score += 8;
            }
        }

        // 城市匹配（0-6分）
        if (prefs.getCities() != null && !prefs.getCities().isEmpty()) {
            if (prefs.getCities().contains(u.getCity())) {
                score += 6;
            }
        }

        // 层次匹配（0-7分）
        if (prefs.getTiers() != null && !prefs.getTiers().isEmpty()) {
            String tier = u.getTier();
            if (tier != null) {
                // 检查是否匹配用户偏好的层次
                boolean tierMatch = prefs.getTiers().stream()
                        .anyMatch(t -> tier.contains(t) || t.contains(tier));
                if (tierMatch) {
                    // 根据层次给不同分数
                    if (tier.contains("985")) {
                        score += 7;
                    } else if (tier.contains("211")) {
                        score += 5;
                    } else if (tier.contains("双一流")) {
                        score += 4;
                    } else {
                        score += 2;
                    }
                }
            }
        }

        return score;
    }

    /**
     * 性价比分（0-5分）
     * 学费越低于用户预算，性价比越高
     */
    private double calcValueScore(UniversityMajorPlan p, RecommendRequest.Preferences prefs) {
        if (p.getTuition() == null) {
            return 2.5; // 无数据时给中间分
        }

        Integer tuitionMax = (prefs != null) ? prefs.getTuitionMax() : null;
        int tuition = p.getTuition();

        if (tuitionMax == null) {
            // 用户未设置预算，按绝对值评分
            // 学费 <= 6000 满分，> 15000 零分
            if (tuition <= 6000) return 5;
            if (tuition >= 15000) return 0;
            return 5.0 * (15000 - tuition) / 9000.0;
        }

        // 用户设置了预算
        if (tuition <= tuitionMax * 0.5) {
            return 5; // 学费低于预算50%，满分
        } else if (tuition <= tuitionMax * 0.8) {
            return 4; // 学费低于预算80%
        } else if (tuition <= tuitionMax) {
            return 3; // 学费在预算内
        } else {
            return 0; // 超预算（理论上已被过滤）
        }
    }

    /**
     * 计算带趋势调整的匹配度（扩展版本）
     * 趋势调整分（-5~+5分）
     * - 上升趋势（更难录取）：-5分
     * - 平稳：0分
     * - 下降趋势（更易录取）：+5分
     */
    private int calcFitScoreWithTrend(University u,
                                       Major m,
                                       UniversityMajorPlan p,
                                       RecommendRequest.Preferences prefs,
                                       int candidateRank,
                                       Integer lastYearMinRank,
                                       String trend) {
        int baseScore = calcFitScore(u, m, p, prefs, candidateRank, lastYearMinRank);
        
        // 趋势调整
        int trendAdjust = 0;
        if ("上".equals(trend)) {
            trendAdjust = -5; // 上升趋势，更难录取
        } else if ("下".equals(trend)) {
            trendAdjust = 5;  // 下降趋势，更易录取
        }
        
        return Math.max(0, Math.min(baseScore + trendAdjust, 100));
    }
}
