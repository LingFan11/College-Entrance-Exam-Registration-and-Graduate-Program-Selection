package com.lingfan.xspp.gaokao.dto;

import java.util.List;

public class RecommendResponse {
    public static class Buckets {
        private List<RecommendationItem> rush;
        private List<RecommendationItem> stable;
        private List<RecommendationItem> safe;

        public List<RecommendationItem> getRush() { return rush; }
        public void setRush(List<RecommendationItem> rush) { this.rush = rush; }
        public List<RecommendationItem> getStable() { return stable; }
        public void setStable(List<RecommendationItem> stable) { this.stable = stable; }
        public List<RecommendationItem> getSafe() { return safe; }
        public void setSafe(List<RecommendationItem> safe) { this.safe = safe; }
    }

    private Buckets buckets;
    private String explain;

    // New: resolved rank metadata for the request (用于前端展示位次与来源)
    private Integer resolvedRank;
    private String rankSource;     // band | mapping_exact | mapping_nearest | clamp_high | clamp_low
    private Integer rankBandMin;   // 若命中 band 或可推导，提供区间下界
    private Integer rankBandMax;   // 区间上界
    private Integer scoreUsed;     // 实际用于映射的分数

    public Buckets getBuckets() { return buckets; }
    public void setBuckets(Buckets buckets) { this.buckets = buckets; }
    public String getExplain() { return explain; }
    public void setExplain(String explain) { this.explain = explain; }
    public Integer getResolvedRank() { return resolvedRank; }
    public void setResolvedRank(Integer resolvedRank) { this.resolvedRank = resolvedRank; }
    public String getRankSource() { return rankSource; }
    public void setRankSource(String rankSource) { this.rankSource = rankSource; }
    public Integer getRankBandMin() { return rankBandMin; }
    public void setRankBandMin(Integer rankBandMin) { this.rankBandMin = rankBandMin; }
    public Integer getRankBandMax() { return rankBandMax; }
    public void setRankBandMax(Integer rankBandMax) { this.rankBandMax = rankBandMax; }
    public Integer getScoreUsed() { return scoreUsed; }
    public void setScoreUsed(Integer scoreUsed) { this.scoreUsed = scoreUsed; }
}
