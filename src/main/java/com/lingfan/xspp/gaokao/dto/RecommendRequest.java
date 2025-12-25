package com.lingfan.xspp.gaokao.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class RecommendRequest {
    @NotBlank
    private String province; // 如 江苏

    @NotBlank
    private String subjectType; // history | physics

    // 新增：科目选择
    // 首选科目（1项）：物理/历史
    private String firstSubject;
    // 次选科目（最多2项）：化学/生物/政治/地理
    private List<String> secondSubjects;

    @Min(0)
    private Integer score; // 可选，与rank二选一

    @Min(0)
    private Integer rank; // 可选，与score二选一

    private Integer year; // 可选，默认最近一年

    private String batch; // 可选，如 本科批

    private Preferences preferences; // 可选

    private String risk; // aggressive | balanced | conservative

    private Integer page; // 可选
    private Integer size; // 可选

    public static class Preferences {
        private List<String> cities;
        private List<String> majors;
        private List<String> tiers; // 985/211/双一流/普通
        private Integer tuitionMax;

        public List<String> getCities() { return cities; }
        public void setCities(List<String> cities) { this.cities = cities; }
        public List<String> getMajors() { return majors; }
        public void setMajors(List<String> majors) { this.majors = majors; }
        public List<String> getTiers() { return tiers; }
        public void setTiers(List<String> tiers) { this.tiers = tiers; }
        public Integer getTuitionMax() { return tuitionMax; }
        public void setTuitionMax(Integer tuitionMax) { this.tuitionMax = tuitionMax; }
    }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getSubjectType() { return subjectType; }
    public void setSubjectType(String subjectType) { this.subjectType = subjectType; }
    public String getFirstSubject() { return firstSubject; }
    public void setFirstSubject(String firstSubject) { this.firstSubject = firstSubject; }
    public List<String> getSecondSubjects() { return secondSubjects; }
    public void setSecondSubjects(List<String> secondSubjects) { this.secondSubjects = secondSubjects; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }
    public Preferences getPreferences() { return preferences; }
    public void setPreferences(Preferences preferences) { this.preferences = preferences; }
    public String getRisk() { return risk; }
    public void setRisk(String risk) { this.risk = risk; }
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
}
