package com.lingfan.xspp.gaokao.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gaokao.recommend")
public class RecommendProperties {
    // score weights
    private int baseScore = 50;
    private int proximityMax = 30;           // 位次接近度最大加分
    private int proximityPenaltyPer100 = 1;  // 每相差100名扣分
    private int prefMajor = 10;
    private int prefCity = 5;
    private int prefTier = 5;

    // risk bucketing thresholds based on delta = candidateRank - lastYearMinRank
    // rush if delta <= rushUpper (negative)
    private int rushUpper = -1000;
    // stable if delta <= stableUpper else safe
    private int stableUpper = 1000;
    // “冲”的近线窗口（位次差绝对值≤该值判定为冲），默认 50，可按省份/年份调整
    private int rushWindowRank = 50;

    // masking rule: top N ranks considered masked (no score required). Beyond this, score is required.
    private int maskTopRank = 50;
    private boolean requireScoreBeyondMask = true;

    // consistency check for rank & score (赋分制一致性)
    private boolean consistencyCheckEnabled = true;
    private int consistencyToleranceRank = 300; // 允许误差（名次）
    private String preferInput = "rank"; // rank | score
    private boolean consistencyStrict = true; // 一致性不满足时是否抛错；为 false 时仅采用 preferInput 不抛错

    // min-score gating & fallbacks
    private boolean useMinScoreGate = false; // 默认关闭，避免无数据时误杀
    private int minScoreGrace = 0;           // 分数容差
    private int rankGrace = 0;               // 当缺分数线时，以位次线判定的容差

    // score->rank 映射：最近邻策略（当找不到精确分数时）
    private boolean mappingNearestEnabled = true; // 开启最近邻映射
    private int mappingMaxScoreDelta = 20;        // 允许分数差（分）上限

    // 边界夹取：当只有单侧邻居（高/低）或超出带宽但接近边界时，允许用边界分数做映射
    private boolean mappingEdgeClampEnabled = true;
    private int mappingEdgeMaxScoreDelta = 40;     // 边界最大允许分差

    // score band → rank range 优先策略
    private boolean useRankBandFirst = true; // 优先采用分数段映射位次区间
    private boolean bandPreferConservative = true; // 使用区间内更保守的位次（rank_max）

    public int getBaseScore() { return baseScore; }
    public void setBaseScore(int baseScore) { this.baseScore = baseScore; }
    public int getProximityMax() { return proximityMax; }
    public void setProximityMax(int proximityMax) { this.proximityMax = proximityMax; }
    public int getProximityPenaltyPer100() { return proximityPenaltyPer100; }
    public void setProximityPenaltyPer100(int proximityPenaltyPer100) { this.proximityPenaltyPer100 = proximityPenaltyPer100; }
    public int getPrefMajor() { return prefMajor; }
    public void setPrefMajor(int prefMajor) { this.prefMajor = prefMajor; }
    public int getPrefCity() { return prefCity; }
    public void setPrefCity(int prefCity) { this.prefCity = prefCity; }
    public int getPrefTier() { return prefTier; }
    public void setPrefTier(int prefTier) { this.prefTier = prefTier; }
    public int getRushUpper() { return rushUpper; }
    public void setRushUpper(int rushUpper) { this.rushUpper = rushUpper; }
    public int getStableUpper() { return stableUpper; }
    public void setStableUpper(int stableUpper) { this.stableUpper = stableUpper; }
    public int getRushWindowRank() { return rushWindowRank; }
    public void setRushWindowRank(int rushWindowRank) { this.rushWindowRank = rushWindowRank; }
    public int getMaskTopRank() { return maskTopRank; }
    public void setMaskTopRank(int maskTopRank) { this.maskTopRank = maskTopRank; }
    public boolean isRequireScoreBeyondMask() { return requireScoreBeyondMask; }
    public void setRequireScoreBeyondMask(boolean requireScoreBeyondMask) { this.requireScoreBeyondMask = requireScoreBeyondMask; }
    public boolean isConsistencyCheckEnabled() { return consistencyCheckEnabled; }
    public void setConsistencyCheckEnabled(boolean consistencyCheckEnabled) { this.consistencyCheckEnabled = consistencyCheckEnabled; }
    public int getConsistencyToleranceRank() { return consistencyToleranceRank; }
    public void setConsistencyToleranceRank(int consistencyToleranceRank) { this.consistencyToleranceRank = consistencyToleranceRank; }
    public String getPreferInput() { return preferInput; }
    public void setPreferInput(String preferInput) { this.preferInput = preferInput; }
    public boolean isConsistencyStrict() { return consistencyStrict; }
    public void setConsistencyStrict(boolean consistencyStrict) { this.consistencyStrict = consistencyStrict; }
    public boolean isUseMinScoreGate() { return useMinScoreGate; }
    public void setUseMinScoreGate(boolean useMinScoreGate) { this.useMinScoreGate = useMinScoreGate; }
    public int getMinScoreGrace() { return minScoreGrace; }
    public void setMinScoreGrace(int minScoreGrace) { this.minScoreGrace = minScoreGrace; }
    public int getRankGrace() { return rankGrace; }
    public void setRankGrace(int rankGrace) { this.rankGrace = rankGrace; }
    public boolean isMappingNearestEnabled() { return mappingNearestEnabled; }
    public void setMappingNearestEnabled(boolean mappingNearestEnabled) { this.mappingNearestEnabled = mappingNearestEnabled; }
    public int getMappingMaxScoreDelta() { return mappingMaxScoreDelta; }
    public void setMappingMaxScoreDelta(int mappingMaxScoreDelta) { this.mappingMaxScoreDelta = mappingMaxScoreDelta; }
    public boolean isMappingEdgeClampEnabled() { return mappingEdgeClampEnabled; }
    public void setMappingEdgeClampEnabled(boolean mappingEdgeClampEnabled) { this.mappingEdgeClampEnabled = mappingEdgeClampEnabled; }
    public int getMappingEdgeMaxScoreDelta() { return mappingEdgeMaxScoreDelta; }
    public void setMappingEdgeMaxScoreDelta(int mappingEdgeMaxScoreDelta) { this.mappingEdgeMaxScoreDelta = mappingEdgeMaxScoreDelta; }
    public boolean isUseRankBandFirst() { return useRankBandFirst; }
    public void setUseRankBandFirst(boolean useRankBandFirst) { this.useRankBandFirst = useRankBandFirst; }
    public boolean isBandPreferConservative() { return bandPreferConservative; }
    public void setBandPreferConservative(boolean bandPreferConservative) { this.bandPreferConservative = bandPreferConservative; }
}
