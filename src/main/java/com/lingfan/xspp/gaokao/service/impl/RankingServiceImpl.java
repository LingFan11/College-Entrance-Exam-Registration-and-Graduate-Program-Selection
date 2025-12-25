package com.lingfan.xspp.gaokao.service.impl;

import com.lingfan.xspp.gaokao.domain.repo.RankMappingRepository;
import com.lingfan.xspp.gaokao.domain.repo.RankBandRepository;
import com.lingfan.xspp.gaokao.exception.BizException;
import com.lingfan.xspp.gaokao.service.RankingService;
import com.lingfan.xspp.gaokao.config.RecommendProperties;
import com.lingfan.xspp.gaokao.dto.RankResolution;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RankingServiceImpl implements RankingService {
    private final RankMappingRepository rankMappingRepository;
    private final RankBandRepository rankBandRepository;
    private final RecommendProperties props;

    public RankingServiceImpl(RankMappingRepository rankMappingRepository,
                              RankBandRepository rankBandRepository,
                              RecommendProperties props) {
        this.rankMappingRepository = rankMappingRepository;
        this.rankBandRepository = rankBandRepository;
        this.props = props;
    }

    @Override
    public int resolveRank(Integer rank, Integer score, String province, String subjectType, Integer year) {
        if (rank != null) {
            // 屏蔽规则：前 N 名无需分数；其他位次必须提供分数
            if (rank <= props.getMaskTopRank()) {
                // 若同时提供 score，且开启一致性校验，则进行一致性校验
                if (props.isConsistencyCheckEnabled() && score != null) {
                    int mapped = mapScoreToRank(score, province, subjectType, year).getRank();
                    if (Math.abs(mapped - rank) > props.getConsistencyToleranceRank()) {
                        if (props.isConsistencyStrict()) {
                            throw new BizException("参数不合适：分数与位次不一致（赋分制约束）");
                        } else {
                            // 软处理：根据 preferInput 采用其中一个
                            return "score".equalsIgnoreCase(props.getPreferInput()) ? mapped : rank;
                        }
                    }
                }
                return rank;
            }
            if (props.isRequireScoreBeyondMask() && score == null) {
                throw new BizException("参数不合适：该位次需提供分数用于屏蔽规则校验");
            }
            // rank > maskTopRank 且提供了 score：根据配置做一致性校验
            if (props.isConsistencyCheckEnabled() && score != null) {
                int mapped = mapScoreToRank(score, province, subjectType, year).getRank();
                if (Math.abs(mapped - rank) > props.getConsistencyToleranceRank()) {
                    if (props.isConsistencyStrict()) {
                        throw new BizException("参数不合适：分数与位次不一致（赋分制约束）");
                    } else {
                        // 软处理：根据 preferInput 采用其中一个
                        return "score".equalsIgnoreCase(props.getPreferInput()) ? mapped : rank;
                    }
                }
                // 一致时可根据 preferInput 选择采用哪个输入
                if ("score".equalsIgnoreCase(props.getPreferInput())) {
                    return mapped;
                }
            }
            return rank;
        }
        if (score == null) {
            throw new BizException("参数不合适：rank/score 至少提供一个");
        }
        if (year == null) {
            year = 2024; // MVP 默认
        }
        return mapScoreToRank(score, province, subjectType, year).getRank();
    }

    @Override
    public RankResolution resolveRankWithMeta(Integer rank, Integer score, String province, String subjectType, Integer year) {
        if (rank != null) {
            if (rank <= props.getMaskTopRank()) {
                // 顶尖区间直接用输入位次，若开启校验且提供分数，则标注 input_rank_checked
                if (props.isConsistencyCheckEnabled() && score != null) {
                    RankResolution mapped = mapScoreToRank(score, province, subjectType, year);
                    if (Math.abs(mapped.getRank() - rank) > props.getConsistencyToleranceRank()) {
                        if (props.isConsistencyStrict()) {
                            throw new BizException("参数不合适：分数与位次不一致（赋分制约束）");
                        } else {
                            return "score".equalsIgnoreCase(props.getPreferInput()) ? mapped : new RankResolution(rank, "input_rank");
                        }
                    }
                    return new RankResolution(rank, "input_rank_checked");
                }
                return new RankResolution(rank, "input_rank");
            }
            if (props.isRequireScoreBeyondMask() && score == null) {
                throw new BizException("参数不合适：该位次需提供分数用于屏蔽规则校验");
            }
            if (props.isConsistencyCheckEnabled() && score != null) {
                RankResolution mapped = mapScoreToRank(score, province, subjectType, year);
                if (Math.abs(mapped.getRank() - rank) > props.getConsistencyToleranceRank()) {
                    if (props.isConsistencyStrict()) {
                        throw new BizException("参数不合适：分数与位次不一致（赋分制约束）");
                    } else {
                        return "score".equalsIgnoreCase(props.getPreferInput()) ? mapped : new RankResolution(rank, "input_rank");
                    }
                }
                if ("score".equalsIgnoreCase(props.getPreferInput())) {
                    return mapped;
                }
            }
            return new RankResolution(rank, "input_rank");
        }
        if (score == null) throw new BizException("参数不合适：rank/score 至少提供一个");
        if (year == null) year = 2024;
        return mapScoreToRank(score, province, subjectType, year);
    }

    private RankResolution mapScoreToRank(Integer score, String province, String subjectType, Integer year) {
        // 1) band 优先：score ∈ [scoreMin, scoreMax] → 取区间位次（保守取 rankMax）
        if (props.isUseRankBandFirst()) {
            Optional<com.lingfan.xspp.gaokao.domain.entity.RankBand> bandOpt = rankBandRepository
                    .findTopByProvinceAndSubjectTypeAndYearAndScoreMinLessThanEqualAndScoreMaxGreaterThanEqualOrderByIdDesc(
                            province, subjectType, year, score, score);
            if (bandOpt.isPresent()) {
                com.lingfan.xspp.gaokao.domain.entity.RankBand band = bandOpt.get();
                if (props.isBandPreferConservative() && band.getRankMax() != null) {
                    RankResolution rr = new RankResolution(band.getRankMax(), "band");
                    rr.setRankBandMin(band.getRankMin());
                    rr.setRankBandMax(band.getRankMax());
                    rr.setScoreUsed(score);
                    return rr;
                } else if (band.getRankMin() != null) {
                    RankResolution rr = new RankResolution(band.getRankMin(), "band");
                    rr.setRankBandMin(band.getRankMin());
                    rr.setRankBandMax(band.getRankMax());
                    rr.setScoreUsed(score);
                    return rr;
                }
                // 若band缺rank信息则继续走 mapping 兜底
            }
        }

        // 2) exact mapping
        Optional<Integer> exact = rankMappingRepository
                .findTopByProvinceAndSubjectTypeAndYearAndScoreOrderByIdDesc(province, subjectType, year, score)
                .map(m -> m.getRankValue());
        if (exact.isPresent()) {
            RankResolution rr = new RankResolution(exact.get(), "mapping_exact");
            rr.setScoreUsed(score);
            return rr;
        }

        // 3) nearest neighbors
        if (props.isMappingNearestEnabled()) {
            Optional<Integer> lower = rankMappingRepository
                    .findTopByProvinceAndSubjectTypeAndYearAndScoreLessThanEqualOrderByScoreDesc(province, subjectType, year, score)
                    .map(m -> m.getRankValue());
            Optional<Integer> upper = rankMappingRepository
                    .findTopByProvinceAndSubjectTypeAndYearAndScoreGreaterThanEqualOrderByScoreAsc(province, subjectType, year, score)
                    .map(m -> m.getRankValue());
            // choose nearest by score distance; if tie, prefer conservative (worse rank = larger number)
            Integer chosen = null;
            Integer lowerScore = lower.isPresent() ? score : null; // we don't have the score diff here; need fetch entities
            // Re-query entities to compute score delta precisely
            Optional<com.lingfan.xspp.gaokao.domain.entity.RankMapping> lowerEnt = rankMappingRepository
                    .findTopByProvinceAndSubjectTypeAndYearAndScoreLessThanEqualOrderByScoreDesc(province, subjectType, year, score);
            Optional<com.lingfan.xspp.gaokao.domain.entity.RankMapping> upperEnt = rankMappingRepository
                    .findTopByProvinceAndSubjectTypeAndYearAndScoreGreaterThanEqualOrderByScoreAsc(province, subjectType, year, score);
            if (lowerEnt.isPresent() || upperEnt.isPresent()) {
                int bestRank;
                int bestDiff = Integer.MAX_VALUE;
                if (lowerEnt.isPresent()) {
                    int diff = Math.abs(score - lowerEnt.get().getScore());
                    if (diff <= props.getMappingMaxScoreDelta()) {
                        bestRank = lowerEnt.get().getRankValue();
                        bestDiff = diff;
                        chosen = bestRank;
                    }
                }
                if (upperEnt.isPresent()) {
                    int diff = Math.abs(score - upperEnt.get().getScore());
                    if (diff <= props.getMappingMaxScoreDelta()) {
                        if (diff < bestDiff) {
                            chosen = upperEnt.get().getRankValue();
                        } else if (diff == bestDiff) {
                            // tie: prefer conservative larger rank value
                            int cand = upperEnt.get().getRankValue();
                            if (chosen == null || cand > chosen) chosen = cand;
                        }
                    }
                }
            }
            if (chosen != null) {
                RankResolution rr = new RankResolution(chosen, "mapping_nearest");
                rr.setScoreUsed(score);
                return rr;
            }

            // Edge clamp: allow clamping to boundary when only one side exists
            if (props.isMappingEdgeClampEnabled()) {
                // score greater than highest known: lowerEnt present, upperEnt absent → clamp to lowerEnt (best rank)
                if (lowerEnt.isPresent() && !upperEnt.isPresent()) {
                    RankResolution rr = new RankResolution(lowerEnt.get().getRankValue(), "clamp_high");
                    rr.setScoreUsed(score);
                    return rr;
                }
                // score lower than lowest known: upperEnt present, lowerEnt absent → clamp to upperEnt (worst rank)
                if (!lowerEnt.isPresent() && upperEnt.isPresent()) {
                    RankResolution rr = new RankResolution(upperEnt.get().getRankValue(), "clamp_low");
                    rr.setScoreUsed(score);
                    return rr;
                }
            }
        }
        throw new BizException("参数不合适：分数无法映射为位次");
    }
}
