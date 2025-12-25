package com.lingfan.xspp.gaokao.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "university_major_stat")
public class UniversityMajorStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "university_id", nullable = false)
    private Long universityId;

    @Column(name = "major_id", nullable = false)
    private Long majorId;

    @Column(nullable = false)
    private String province;

    @Column(name = "subject_type", nullable = false)
    private String subjectType;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "min_score")
    private Integer minScore;

    @Column(name = "min_rank")
    private Integer minRank;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUniversityId() { return universityId; }
    public void setUniversityId(Long universityId) { this.universityId = universityId; }
    public Long getMajorId() { return majorId; }
    public void setMajorId(Long majorId) { this.majorId = majorId; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getSubjectType() { return subjectType; }
    public void setSubjectType(String subjectType) { this.subjectType = subjectType; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public Integer getMinScore() { return minScore; }
    public void setMinScore(Integer minScore) { this.minScore = minScore; }
    public Integer getMinRank() { return minRank; }
    public void setMinRank(Integer minRank) { this.minRank = minRank; }
}
