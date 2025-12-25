package com.lingfan.xspp.grad.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "grad_student_profile")
public class GradStudentProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private String currentUniversity;
    private String currentMajor;

    private Double gpa;
    private Integer englishScore;
    private String englishType;

    @Lob
    private String competitions;

    @Lob
    private String researchExp;

    @Lob
    private String preferredDirections; // JSON array of direction ids

    private String regionPref;
    private String stylePref;
    private String targetTier;

    @Lob
    private String targetUniversities; // JSON

    @Lob
    private String targetDirectionsTopn; // JSON array of {directionId, weight}

    // 考研初试成绩字段 (Requirements 1.1, 1.2)
    @Column(name = "exam_total_score")
    private Integer examTotalScore;  // 初试总分 (0-500)

    @Column(name = "politics_score")
    private Integer politicsScore;   // 政治成绩 (0-100)

    @Column(name = "english_exam_score")
    private Integer englishExamScore; // 英语考试成绩 (0-100)，区别于 englishScore（证书分数）

    @Column(name = "math_score")
    private Integer mathScore;       // 数学成绩 (0-150)

    @Column(name = "professional_score")
    private Integer professionalScore; // 专业课成绩 (0-150)

    @Column(name = "target_institution_group_id")
    private Long targetInstitutionGroupId; // 目标机构分组ID

    private Instant updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getCurrentUniversity() { return currentUniversity; }
    public void setCurrentUniversity(String currentUniversity) { this.currentUniversity = currentUniversity; }
    public String getCurrentMajor() { return currentMajor; }
    public void setCurrentMajor(String currentMajor) { this.currentMajor = currentMajor; }
    public Double getGpa() { return gpa; }
    public void setGpa(Double gpa) { this.gpa = gpa; }
    public Integer getEnglishScore() { return englishScore; }
    public void setEnglishScore(Integer englishScore) { this.englishScore = englishScore; }
    public String getEnglishType() { return englishType; }
    public void setEnglishType(String englishType) { this.englishType = englishType; }
    public String getCompetitions() { return competitions; }
    public void setCompetitions(String competitions) { this.competitions = competitions; }
    public String getResearchExp() { return researchExp; }
    public void setResearchExp(String researchExp) { this.researchExp = researchExp; }
    public String getPreferredDirections() { return preferredDirections; }
    public void setPreferredDirections(String preferredDirections) { this.preferredDirections = preferredDirections; }
    public String getRegionPref() { return regionPref; }
    public void setRegionPref(String regionPref) { this.regionPref = regionPref; }
    public String getStylePref() { return stylePref; }
    public void setStylePref(String stylePref) { this.stylePref = stylePref; }
    public String getTargetTier() { return targetTier; }
    public void setTargetTier(String targetTier) { this.targetTier = targetTier; }
    public String getTargetUniversities() { return targetUniversities; }
    public void setTargetUniversities(String targetUniversities) { this.targetUniversities = targetUniversities; }
    public String getTargetDirectionsTopn() { return targetDirectionsTopn; }
    public void setTargetDirectionsTopn(String targetDirectionsTopn) { this.targetDirectionsTopn = targetDirectionsTopn; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    // 考研初试成绩字段 getter/setter (Requirements 1.1, 1.2)
    public Integer getExamTotalScore() { return examTotalScore; }
    public void setExamTotalScore(Integer examTotalScore) { this.examTotalScore = examTotalScore; }
    public Integer getPoliticsScore() { return politicsScore; }
    public void setPoliticsScore(Integer politicsScore) { this.politicsScore = politicsScore; }
    public Integer getEnglishExamScore() { return englishExamScore; }
    public void setEnglishExamScore(Integer englishExamScore) { this.englishExamScore = englishExamScore; }
    public Integer getMathScore() { return mathScore; }
    public void setMathScore(Integer mathScore) { this.mathScore = mathScore; }
    public Integer getProfessionalScore() { return professionalScore; }
    public void setProfessionalScore(Integer professionalScore) { this.professionalScore = professionalScore; }
    public Long getTargetInstitutionGroupId() { return targetInstitutionGroupId; }
    public void setTargetInstitutionGroupId(Long targetInstitutionGroupId) { this.targetInstitutionGroupId = targetInstitutionGroupId; }
}
