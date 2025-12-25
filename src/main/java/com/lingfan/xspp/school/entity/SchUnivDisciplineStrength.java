package com.lingfan.xspp.school.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "sch_univ_discipline_strength")
@IdClass(SchUnivDisciplineStrength.StrengthId.class)
public class SchUnivDisciplineStrength {

    @Id
    @Column(name = "university_id")
    private Long universityId;

    @Id
    @Column(name = "discipline_id")
    private Long disciplineId;

    @Id
    @Column(name = "year")
    private Integer year;

    @Column(name = "rank_grade")
    private String rankGrade;

    @Column(name = "rank_value")
    private Integer rankValue;

    private String source;
    private String note;

    public Long getUniversityId() { return universityId; }
    public void setUniversityId(Long universityId) { this.universityId = universityId; }
    public Long getDisciplineId() { return disciplineId; }
    public void setDisciplineId(Long disciplineId) { this.disciplineId = disciplineId; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public String getRankGrade() { return rankGrade; }
    public void setRankGrade(String rankGrade) { this.rankGrade = rankGrade; }
    public Integer getRankValue() { return rankValue; }
    public void setRankValue(Integer rankValue) { this.rankValue = rankValue; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public static class StrengthId implements Serializable {
        private Long universityId;
        private Long disciplineId;
        private Integer year;
        public StrengthId() {}
        public StrengthId(Long universityId, Long disciplineId, Integer year) {
            this.universityId = universityId;
            this.disciplineId = disciplineId;
            this.year = year;
        }
        public Long getUniversityId() { return universityId; }
        public void setUniversityId(Long universityId) { this.universityId = universityId; }
        public Long getDisciplineId() { return disciplineId; }
        public void setDisciplineId(Long disciplineId) { this.disciplineId = disciplineId; }
        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StrengthId that = (StrengthId) o;
            return java.util.Objects.equals(universityId, that.universityId)
                    && java.util.Objects.equals(disciplineId, that.disciplineId)
                    && java.util.Objects.equals(year, that.year);
        }
        @Override public int hashCode() {
            return java.util.Objects.hash(universityId, disciplineId, year);
        }
    }
}
