package com.lingfan.xspp.gaokao.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "university_major_plan")
public class UniversityMajorPlan {
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
    private String subjectType; // history|physics

    @Column(nullable = false)
    private String batch; // 本科批

    @Column(nullable = false)
    private Integer tuition; // 每年学费（元）

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "plan_count", nullable = false)
    private Integer planCount;

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
    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }
    public Integer getTuition() { return tuition; }
    public void setTuition(Integer tuition) { this.tuition = tuition; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public Integer getPlanCount() { return planCount; }
    public void setPlanCount(Integer planCount) { this.planCount = planCount; }
}
