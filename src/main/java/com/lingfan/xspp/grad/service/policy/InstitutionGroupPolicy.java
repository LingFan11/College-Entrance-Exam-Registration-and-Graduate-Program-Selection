package com.lingfan.xspp.grad.service.policy;

import com.lingfan.xspp.grad.entity.GradStudentProfile;

import java.util.Set;

/**
 * 机构分组过滤策略 (Requirements 4.4)
 * 当学生选择了目标机构分组时，过滤不在该分组内的导师
 * 
 * 此策略需要外部提供导师所属分组信息，通过 PolicyContext 扩展传入
 */
public class InstitutionGroupPolicy implements RecommendPolicy {

    private final Set<Long> mentorGroupIds;

    /**
     * 构造函数
     * @param mentorGroupIds 当前导师所属的分组ID集合
     */
    public InstitutionGroupPolicy(Set<Long> mentorGroupIds) {
        this.mentorGroupIds = mentorGroupIds != null ? mentorGroupIds : Set.of();
    }

    @Override
    public void apply(PolicyContext ctx, ScoreAccumulator acc) {
        GradStudentProfile profile = ctx.getProfile();
        
        // 如果学生没有选择目标机构分组，则不进行过滤
        Long targetGroupId = profile.getTargetInstitutionGroupId();
        if (targetGroupId == null) {
            return;
        }

        // 如果导师没有分组信息（可能是普通高校导师），则不进行过滤
        // 根据 Requirements 5.4: 机构无分组配置时，所有导师视为同一分组
        if (mentorGroupIds.isEmpty()) {
            return;
        }

        // 检查导师是否属于学生的目标分组
        if (!mentorGroupIds.contains(targetGroupId)) {
            acc.addReason("导师不在目标机构分组内");
            acc.setStatusAtLeast("unfit");
        }
    }

    @Override
    public String name() {
        return "InstitutionGroupPolicy";
    }
}
