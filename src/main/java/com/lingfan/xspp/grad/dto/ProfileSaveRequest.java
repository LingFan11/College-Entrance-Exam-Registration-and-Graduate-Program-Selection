package com.lingfan.xspp.grad.dto;

import jakarta.validation.constraints.*;

public class ProfileSaveRequest {
    @NotNull
    @Positive
    private Long userId;

    @Size(max = 128)
    private String currentUniversity;

    @Size(max = 128)
    private String currentMajor;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "5.0", inclusive = true)
    private Double gpa;

    @Min(0)
    private Integer englishScore;

    @Size(max = 32)
    private String englishType; // CET4/CET6/IELTS/TOEFL

    @Size(max = 2000)
    private String competitions; // free text

    @Size(max = 4000)
    private String researchExp; // free text

    @Size(max = 2000)
    private String preferredDirections; // JSON string

    @Size(max = 64)
    private String regionPref;

    @Size(max = 64)
    private String stylePref;

    @Size(max = 16)
    private String targetTier;

    @Size(max = 2000)
    private String targetUniversities; // JSON string

    @Size(max = 2000)
    private String targetDirectionsTopn; // JSON string of [{directionId, weight}]

    // 考研初试成绩字段 (Requirements 1.1, 1.2, 1.5)
    @Min(value = 0, message = "总分必须在0-500之间")
    @Max(value = 500, message = "总分必须在0-500之间")
    private Integer examTotalScore;  // 初试总分 (0-500)

    @Min(value = 0, message = "政治分数必须在0-100之间")
    @Max(value = 100, message = "政治分数必须在0-100之间")
    private Integer politicsScore;   // 政治成绩 (0-100)

    @Min(value = 0, message = "英语分数必须在0-100之间")
    @Max(value = 100, message = "英语分数必须在0-100之间")
    private Integer englishExamScore; // 英语考试成绩 (0-100)

    @Min(value = 0, message = "数学分数必须在0-150之间")
    @Max(value = 150, message = "数学分数必须在0-150之间")
    private Integer mathScore;       // 数学成绩 (0-150)

    @Min(value = 0, message = "专业课分数必须在0-150之间")
    @Max(value = 150, message = "专业课分数必须在0-150之间")
    private Integer professionalScore; // 专业课成绩 (0-150)

    private Long targetInstitutionGroupId; // 目标机构分组ID

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

    // 考研初试成绩字段 getter/setter (Requirements 1.1, 1.2, 1.5)
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
