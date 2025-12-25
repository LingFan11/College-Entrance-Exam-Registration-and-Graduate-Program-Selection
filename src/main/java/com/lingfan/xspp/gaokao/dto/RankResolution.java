package com.lingfan.xspp.gaokao.dto;

public class RankResolution {
    private int rank;              // 最终用于匹配/分桶的位次
    private String source;         // band | mapping_exact | mapping_nearest | clamp_high | clamp_low | input_rank | input_rank_checked
    private Integer rankBandMin;   // 命中 band 时的位次下界
    private Integer rankBandMax;   // 命中 band 时的位次上界
    private Integer scoreUsed;     // 实际用于映射的分数（若采用score侧）

    public RankResolution() {}
    public RankResolution(int rank, String source) { this.rank = rank; this.source = source; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Integer getRankBandMin() { return rankBandMin; }
    public void setRankBandMin(Integer rankBandMin) { this.rankBandMin = rankBandMin; }
    public Integer getRankBandMax() { return rankBandMax; }
    public void setRankBandMax(Integer rankBandMax) { this.rankBandMax = rankBandMax; }
    public Integer getScoreUsed() { return scoreUsed; }
    public void setScoreUsed(Integer scoreUsed) { this.scoreUsed = scoreUsed; }
}
