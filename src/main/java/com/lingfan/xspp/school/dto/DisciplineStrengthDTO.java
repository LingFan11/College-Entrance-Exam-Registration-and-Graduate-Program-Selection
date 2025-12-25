package com.lingfan.xspp.school.dto;

public class DisciplineStrengthDTO {
    private Long disciplineId;
    private String disciplineName;
    private String rankGrade;
    private Integer rankValue;
    private String source;
    private Integer year;

    public Long getDisciplineId() { return disciplineId; }
    public void setDisciplineId(Long disciplineId) { this.disciplineId = disciplineId; }
    public String getDisciplineName() { return disciplineName; }
    public void setDisciplineName(String disciplineName) { this.disciplineName = disciplineName; }
    public String getRankGrade() { return rankGrade; }
    public void setRankGrade(String rankGrade) { this.rankGrade = rankGrade; }
    public Integer getRankValue() { return rankValue; }
    public void setRankValue(Integer rankValue) { this.rankValue = rankValue; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
}
