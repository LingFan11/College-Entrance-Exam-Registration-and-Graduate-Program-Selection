package com.lingfan.xspp.gaokao.dto;

public class RecommendationItem {
    private Long universityId;
    private String universityName;
    private String city;
    private String tier;

    private Long majorId;
    private String majorName;
    private String subjectType;
    private String batch;

    private Integer tuition;
    private Integer lastYearMinRank;
    private Integer lastYearMinScore;
    private String admitTrend; // 上/平/下 (MVP: 平)
    private Integer fitScore; // 0-100

    public Long getUniversityId() { return universityId; }
    public void setUniversityId(Long universityId) { this.universityId = universityId; }
    public String getUniversityName() { return universityName; }
    public void setUniversityName(String universityName) { this.universityName = universityName; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
    public Long getMajorId() { return majorId; }
    public void setMajorId(Long majorId) { this.majorId = majorId; }
    public String getMajorName() { return majorName; }
    public void setMajorName(String majorName) { this.majorName = majorName; }
    public String getSubjectType() { return subjectType; }
    public void setSubjectType(String subjectType) { this.subjectType = subjectType; }
    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }
    public Integer getTuition() { return tuition; }
    public void setTuition(Integer tuition) { this.tuition = tuition; }
    public Integer getLastYearMinRank() { return lastYearMinRank; }
    public void setLastYearMinRank(Integer lastYearMinRank) { this.lastYearMinRank = lastYearMinRank; }
    public Integer getLastYearMinScore() { return lastYearMinScore; }
    public void setLastYearMinScore(Integer lastYearMinScore) { this.lastYearMinScore = lastYearMinScore; }
    public String getAdmitTrend() { return admitTrend; }
    public void setAdmitTrend(String admitTrend) { this.admitTrend = admitTrend; }
    public Integer getFitScore() { return fitScore; }
    public void setFitScore(Integer fitScore) { this.fitScore = fitScore; }
}
