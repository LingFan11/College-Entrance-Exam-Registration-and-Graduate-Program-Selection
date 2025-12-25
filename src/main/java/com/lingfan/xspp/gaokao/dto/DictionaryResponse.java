package com.lingfan.xspp.gaokao.dto;

import java.util.List;

public class DictionaryResponse {
    private List<String> provinces;
    private List<String> subjectTypes;
    // 新增：科目选择字典
    private List<String> firstSubjects;   // 首选科目（物理/历史）
    private List<String> secondSubjects;  // 次选科目（化学/生物/政治/地理）
    private List<String> batches;
    private List<String> tiers;
    private List<String> majorCategories;
    private List<String> cities;

    public List<String> getProvinces() { return provinces; }
    public void setProvinces(List<String> provinces) { this.provinces = provinces; }
    public List<String> getSubjectTypes() { return subjectTypes; }
    public void setSubjectTypes(List<String> subjectTypes) { this.subjectTypes = subjectTypes; }
    public List<String> getFirstSubjects() { return firstSubjects; }
    public void setFirstSubjects(List<String> firstSubjects) { this.firstSubjects = firstSubjects; }
    public List<String> getSecondSubjects() { return secondSubjects; }
    public void setSecondSubjects(List<String> secondSubjects) { this.secondSubjects = secondSubjects; }
    public List<String> getBatches() { return batches; }
    public void setBatches(List<String> batches) { this.batches = batches; }
    public List<String> getTiers() { return tiers; }
    public void setTiers(List<String> tiers) { this.tiers = tiers; }
    public List<String> getMajorCategories() { return majorCategories; }
    public void setMajorCategories(List<String> majorCategories) { this.majorCategories = majorCategories; }
    public List<String> getCities() { return cities; }
    public void setCities(List<String> cities) { this.cities = cities; }
}
