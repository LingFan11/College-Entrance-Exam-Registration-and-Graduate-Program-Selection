package com.lingfan.xspp.grad.service.policy;

import com.lingfan.xspp.grad.entity.GradMentorRequirement;

public class ThresholdPolicy implements RecommendPolicy {
    @Override
    public void apply(PolicyContext ctx, ScoreAccumulator acc) {
        GradMentorRequirement req = ctx.getRequirement();
        if (req == null) return;
        Double minGpa = req.getMinGpa();
        String englishType = req.getEnglishType();
        Integer minEnglish = req.getMinEnglish();

        boolean requiresGpa = (minGpa != null && minGpa > 0);
        boolean requiresEngType = (englishType != null && !englishType.isBlank());
        boolean requiresEngMin = (minEnglish != null && minEnglish > 0);

        // GPA
        if (requiresGpa) {
            if (ctx.getProfile().getGpa() == null) {
                acc.addReason("缺少GPA，无法满足导师门槛 min " + minGpa);
                acc.setStatusAtLeast("unfit");
            } else if (ctx.getProfile().getGpa() < minGpa) {
                acc.addReason("GPA不足 min " + minGpa);
                acc.setStatusAtLeast("unfit");
            }
        }
        // English
        String stuType = ctx.getProfile().getEnglishType();
        Integer stuScore = ctx.getProfile().getEnglishScore();
        if (requiresEngType && requiresEngMin) {
            if (stuType == null || !stuType.equalsIgnoreCase(englishType)) {
                acc.addReason("英语类型要求 " + englishType);
                acc.setStatusAtLeast("unfit");
            } else if (stuScore == null || stuScore < minEnglish) {
                acc.addReason("英语(" + englishType + ")不足 min " + minEnglish);
                acc.setStatusAtLeast("unfit");
            }
        } else if (requiresEngType) {
            if (stuType == null || !stuType.equalsIgnoreCase(englishType)) {
                acc.addReason("英语类型要求 " + englishType);
                acc.setStatusAtLeast("unfit");
            }
        } else if (requiresEngMin) {
            if (stuScore == null || stuScore < minEnglish) {
                acc.addReason("英语分数不足 min " + minEnglish);
                acc.setStatusAtLeast("unfit");
            }
        }
    }

    @Override
    public String name() { return "ThresholdPolicy"; }
}
