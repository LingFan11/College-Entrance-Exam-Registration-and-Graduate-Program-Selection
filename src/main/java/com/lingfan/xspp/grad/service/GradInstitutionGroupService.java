package com.lingfan.xspp.grad.service;

import com.lingfan.xspp.grad.dto.GroupMatchResponse;
import com.lingfan.xspp.grad.dto.InstitutionGroupDTO;

import java.util.List;

/**
 * 机构分组服务接口
 * Requirements: 4.1, 4.2, 4.3, 5.3
 */
public interface GradInstitutionGroupService {

    /**
     * 获取指定机构的所有分组，按最低分降序排列
     * Requirements: 4.1, 5.3
     * 
     * @param institutionCode 机构代码，如 'CAS' 表示中科院
     * @return 分组列表，按 minTotalScore 降序排列
     */
    List<InstitutionGroupDTO> getGroupsByInstitution(String institutionCode);

    /**
     * 根据学生总分匹配最佳分组
     * Requirements: 4.2, 4.3
     * 
     * @param institutionCode 机构代码
     * @param totalScore 学生初试总分
     * @return 匹配结果，包含最佳匹配分组和备选分组
     */
    GroupMatchResponse matchGroup(String institutionCode, Integer totalScore);

    /**
     * 根据ID获取分组信息
     * 
     * @param groupId 分组ID
     * @return 分组DTO，如果不存在返回null
     */
    InstitutionGroupDTO getGroupById(Long groupId);
}
