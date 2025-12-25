package com.lingfan.xspp.grad.dto;

/**
 * 机构分组数据传输对象
 * Requirements: 4.1, 5.3
 * 
 * 用于返回机构分组信息给前端，支持分组选择和展示
 */
public class InstitutionGroupDTO {
    
    private Long id;
    private String institutionCode;   // 机构代码，如 'CAS'
    private String institutionName;   // 机构名称，如 '中国科学院'
    private String groupName;         // 分组名称，如 '计算所'
    private Integer minTotalScore;    // 该分组最低总分要求
    private Integer maxTotalScore;    // 该分组最高总分
    private Integer priority;         // 优先级
    private String description;       // 分组描述

    public InstitutionGroupDTO() {}

    public InstitutionGroupDTO(Long id, String institutionCode, String institutionName,
                               String groupName, Integer minTotalScore, Integer maxTotalScore,
                               Integer priority, String description) {
        this.id = id;
        this.institutionCode = institutionCode;
        this.institutionName = institutionName;
        this.groupName = groupName;
        this.minTotalScore = minTotalScore;
        this.maxTotalScore = maxTotalScore;
        this.priority = priority;
        this.description = description;
    }

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

    /**
     * 从实体类创建 DTO
     */
    public static InstitutionGroupDTO fromEntity(com.lingfan.xspp.grad.entity.GradInstitutionGroup entity) {
        if (entity == null) return null;
        return new InstitutionGroupDTO(
            entity.getId(),
            entity.getInstitutionCode(),
            entity.getInstitutionName(),
            entity.getGroupName(),
            entity.getMinTotalScore(),
            entity.getMaxTotalScore(),
            entity.getPriority(),
            entity.getDescription()
        );
    }
}
