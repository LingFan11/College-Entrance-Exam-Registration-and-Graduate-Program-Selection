package com.lingfan.xspp.grad.service.impl;

import com.lingfan.xspp.grad.dto.GroupMatchResponse;
import com.lingfan.xspp.grad.dto.InstitutionGroupDTO;
import com.lingfan.xspp.grad.entity.GradInstitutionGroup;
import com.lingfan.xspp.grad.repository.GradInstitutionGroupRepository;
import com.lingfan.xspp.grad.service.GradInstitutionGroupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 机构分组服务实现
 * Requirements: 4.1, 4.2, 4.3, 5.3
 */
@Service
public class GradInstitutionGroupServiceImpl implements GradInstitutionGroupService {

    private final GradInstitutionGroupRepository repository;

    public GradInstitutionGroupServiceImpl(GradInstitutionGroupRepository repository) {
        this.repository = repository;
    }

    /**
     * 获取指定机构的所有分组，按最低分降序排列
     * Requirements: 4.1, 5.3
     */
    @Override
    public List<InstitutionGroupDTO> getGroupsByInstitution(String institutionCode) {
        List<GradInstitutionGroup> groups = repository
                .findByInstitutionCodeOrderByMinTotalScoreDesc(institutionCode);
        return groups.stream()
                .map(InstitutionGroupDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据学生总分匹配最佳分组
     * Requirements: 4.2, 4.3
     * 
     * 匹配逻辑：
     * 1. 查找分数在 [minTotalScore, maxTotalScore] 范围内的所有分组
     * 2. 如果有多个匹配，选择优先级最高的分组作为最佳匹配
     * 3. 其他匹配的分组作为备选
     * 4. 如果没有匹配，返回提示信息和所有可用分组作为备选
     */
    @Override
    public GroupMatchResponse matchGroup(String institutionCode, Integer totalScore) {
        if (totalScore == null) {
            List<InstitutionGroupDTO> allGroups = getGroupsByInstitution(institutionCode);
            return GroupMatchResponse.noMatch("请提供初试总分以匹配分组", allGroups);
        }

        // 查找所有匹配的分组（按优先级和最低分降序排列）
        List<GradInstitutionGroup> matchingGroups = repository
                .findMatchingGroups(institutionCode, totalScore);

        if (matchingGroups.isEmpty()) {
            // 没有匹配的分组，返回所有分组作为备选
            List<InstitutionGroupDTO> allGroups = getGroupsByInstitution(institutionCode);
            return GroupMatchResponse.noMatch(
                    "您的成绩暂无匹配的分组，建议查看其他机构或分组",
                    allGroups
            );
        }

        // 第一个是最佳匹配（优先级最高）
        InstitutionGroupDTO bestMatch = InstitutionGroupDTO.fromEntity(matchingGroups.get(0));

        // 其余的是备选分组
        List<InstitutionGroupDTO> alternatives = matchingGroups.stream()
                .skip(1)
                .map(InstitutionGroupDTO::fromEntity)
                .collect(Collectors.toList());

        return GroupMatchResponse.matched(bestMatch, alternatives);
    }

    /**
     * 根据ID获取分组信息
     */
    @Override
    public InstitutionGroupDTO getGroupById(Long groupId) {
        if (groupId == null) {
            return null;
        }
        Optional<GradInstitutionGroup> group = repository.findById(groupId);
        return group.map(InstitutionGroupDTO::fromEntity).orElse(null);
    }
}
