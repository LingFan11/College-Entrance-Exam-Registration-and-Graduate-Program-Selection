package com.lingfan.xspp.grad.dto;

import java.util.List;

/**
 * 分组匹配结果数据传输对象
 * Requirements: 4.2, 4.3
 * 
 * 用于返回学生成绩与机构分组的匹配结果
 */
public class GroupMatchResponse {
    
    /**
     * 匹配到的分组（根据学生总分匹配的最佳分组）
     * 如果学生成绩落在多个分组范围内，返回优先级最高的分组
     */
    private InstitutionGroupDTO matchedGroup;
    
    /**
     * 备选分组列表（其他可能匹配的分组）
     * 按优先级降序排列
     */
    private List<InstitutionGroupDTO> alternativeGroups;
    
    /**
     * 匹配状态消息
     * 如：无匹配分组时的提示信息
     */
    private String message;

    public GroupMatchResponse() {}

    public GroupMatchResponse(InstitutionGroupDTO matchedGroup, 
                              List<InstitutionGroupDTO> alternativeGroups,
                              String message) {
        this.matchedGroup = matchedGroup;
        this.alternativeGroups = alternativeGroups;
        this.message = message;
    }

    /**
     * 创建成功匹配的响应
     */
    public static GroupMatchResponse matched(InstitutionGroupDTO matchedGroup, 
                                             List<InstitutionGroupDTO> alternatives) {
        return new GroupMatchResponse(matchedGroup, alternatives, null);
    }

    /**
     * 创建无匹配的响应
     */
    public static GroupMatchResponse noMatch(String message, List<InstitutionGroupDTO> alternatives) {
        return new GroupMatchResponse(null, alternatives, message);
    }

    // Getters and Setters
    public InstitutionGroupDTO getMatchedGroup() { return matchedGroup; }
    public void setMatchedGroup(InstitutionGroupDTO matchedGroup) { this.matchedGroup = matchedGroup; }

    public List<InstitutionGroupDTO> getAlternativeGroups() { return alternativeGroups; }
    public void setAlternativeGroups(List<InstitutionGroupDTO> alternativeGroups) { 
        this.alternativeGroups = alternativeGroups; 
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
