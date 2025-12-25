package com.lingfan.xspp.grad.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "grad_mentor_requirement")
public class GradMentorRequirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long mentorId;

    private Double minGpa;
    private String englishType; // CET6/IELTS/TOEFL
    private Integer minEnglish;

    // 考研初试科目门槛字段 (Requirements 2.1, 2.2)
    @Column(name = "min_politics")
    private Integer minPolitics;       // 政治最低分 (0-100)

    @Column(name = "min_english_exam")
    private Integer minEnglishExam;    // 英语考试最低分 (0-100)

    @Column(name = "min_math")
    private Integer minMath;           // 数学最低分 (0-150)

    @Column(name = "min_professional")
    private Integer minProfessional;   // 专业课最低分 (0-150)

    @Column(name = "min_total_score")
    private Integer minTotalScore;     // 总分最低分 (0-500)

    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMentorId() { return mentorId; }
    public void setMentorId(Long mentorId) { this.mentorId = mentorId; }
    public Double getMinGpa() { return minGpa; }
    public void setMinGpa(Double minGpa) { this.minGpa = minGpa; }
    public String getEnglishType() { return englishType; }
    public void setEnglishType(String englishType) { this.englishType = englishType; }
    public Integer getMinEnglish() { return minEnglish; }
    public void setMinEnglish(Integer minEnglish) { this.minEnglish = minEnglish; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // 考研初试科目门槛字段 getter/setter (Requirements 2.1, 2.2)
    public Integer getMinPolitics() { return minPolitics; }
    public void setMinPolitics(Integer minPolitics) { this.minPolitics = minPolitics; }
    public Integer getMinEnglishExam() { return minEnglishExam; }
    public void setMinEnglishExam(Integer minEnglishExam) { this.minEnglishExam = minEnglishExam; }
    public Integer getMinMath() { return minMath; }
    public void setMinMath(Integer minMath) { this.minMath = minMath; }
    public Integer getMinProfessional() { return minProfessional; }
    public void setMinProfessional(Integer minProfessional) { this.minProfessional = minProfessional; }
    public Integer getMinTotalScore() { return minTotalScore; }
    public void setMinTotalScore(Integer minTotalScore) { this.minTotalScore = minTotalScore; }
}
