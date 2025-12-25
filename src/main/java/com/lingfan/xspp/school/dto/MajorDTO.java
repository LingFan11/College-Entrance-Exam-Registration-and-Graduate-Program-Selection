package com.lingfan.xspp.school.dto;

public class MajorDTO {
    private Long id;
    private String name;
    private String degree;
    private Long disciplineId;
    private String departmentName;
    private String featureTag;
    private String code;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }
    public Long getDisciplineId() { return disciplineId; }
    public void setDisciplineId(Long disciplineId) { this.disciplineId = disciplineId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getFeatureTag() { return featureTag; }
    public void setFeatureTag(String featureTag) { this.featureTag = featureTag; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
