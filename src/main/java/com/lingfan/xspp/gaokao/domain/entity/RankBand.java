package com.lingfan.xspp.gaokao.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rank_band")
public class RankBand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String province;
    private String subjectType;
    private Integer year;

    private Integer scoreMin;
    private Integer scoreMax;
    private Integer rankMin;
    private Integer rankMax;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getSubjectType() { return subjectType; }
    public void setSubjectType(String subjectType) { this.subjectType = subjectType; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public Integer getScoreMin() { return scoreMin; }
    public void setScoreMin(Integer scoreMin) { this.scoreMin = scoreMin; }
    public Integer getScoreMax() { return scoreMax; }
    public void setScoreMax(Integer scoreMax) { this.scoreMax = scoreMax; }
    public Integer getRankMin() { return rankMin; }
    public void setRankMin(Integer rankMin) { this.rankMin = rankMin; }
    public Integer getRankMax() { return rankMax; }
    public void setRankMax(Integer rankMax) { this.rankMax = rankMax; }
}
