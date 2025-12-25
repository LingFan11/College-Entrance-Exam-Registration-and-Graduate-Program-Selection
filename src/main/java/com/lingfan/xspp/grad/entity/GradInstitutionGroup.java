package com.lingfan.xspp.grad.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * 机构分组实体类 (Requirements 5.1)
 * 用于特殊机构（如中科院）的研究所分组管理
 */
@Entity
@Table(name = "grad_institution_group")
public class GradInstitutionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "institution_code", nullable = false, length = 32)
    private String institutionCode;  // 机构代码，如 'CAS' 表示中科院

    @Column(name = "institution_name", nullable = false, length = 128)
    private String institutionName;  // 机构名称

    @Column(name = "group_name", nullable = false, length = 128)
    private String groupName;        // 分组名称，如 '计算所'、'软件所'

    @Column(name = "min_total_score")
    private Integer minTotalScore;   // 该分组最低总分要求

    @Column(name = "max_total_score")
    private Integer maxTotalScore;   // 该分组最高总分（用于范围匹配）

    @Column(name = "priority")
    private Integer priority = 0;    // 优先级，用于多范围重叠时选择

    @Column(name = "description", length = 512)
    private String description;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInstitutionCode() { return institutionCode; }
    public void setInstitutionCode(String institutionCode) { this.institutionCode = institutionCode; }

    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public Integer getMinTotalScore() { return minTotalScore; }
    public void setMinTotalScore(Integer minTotalScore) { this.minTotalScore = minTotalScore; }

    public Integer getMaxTotalScore() { return maxTotalScore; }
    public void setMaxTotalScore(Integer maxTotalScore) { this.maxTotalScore = maxTotalScore; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
