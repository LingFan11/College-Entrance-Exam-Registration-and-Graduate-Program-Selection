package com.lingfan.xspp.school.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sch_major")
public class SchMajor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, length = 16)
    private String degree; // 本科/硕士/博士

    @Column(name = "discipline_id")
    private Long disciplineId;

    @Column(length = 32)
    private String code;

    @Column(length = 512)
    private String brief;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }
    public Long getDisciplineId() { return disciplineId; }
    public void setDisciplineId(Long disciplineId) { this.disciplineId = disciplineId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getBrief() { return brief; }
    public void setBrief(String brief) { this.brief = brief; }
}
