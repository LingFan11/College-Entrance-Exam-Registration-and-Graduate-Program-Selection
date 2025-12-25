package com.lingfan.xspp.gaokao.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rank_mapping")
public class RankMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String province;

    @Column(name = "subject_type", nullable = false)
    private String subjectType;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "rank_value", nullable = false)
    private Integer rankValue;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getSubjectType() { return subjectType; }
    public void setSubjectType(String subjectType) { this.subjectType = subjectType; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Integer getRankValue() { return rankValue; }
    public void setRankValue(Integer rankValue) { this.rankValue = rankValue; }
}
