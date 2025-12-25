package com.lingfan.xspp.grad.service.policy;

public class MissingInfoPenaltyPolicy implements RecommendPolicy {
    @Override
    public void apply(PolicyContext ctx, ScoreAccumulator acc) {
        int missPenalty = 0;
        if (ctx.getProfile().getCurrentMajor() == null || ctx.getProfile().getCurrentMajor().isBlank()) {
            missPenalty += ctx.getProps().getMissingInfoPenalty();
            acc.addReason("缺少专业信息");
        }
        if (ctx.getProfile().getGpa() == null) {
            missPenalty += ctx.getProps().getMissingInfoPenalty();
            acc.addReason("缺少GPA");
        }
        if (ctx.getProfile().getEnglishType() == null || ctx.getProfile().getEnglishType().isBlank()
                || ctx.getProfile().getEnglishScore() == null) {
            missPenalty += ctx.getProps().getMissingInfoPenalty();
            acc.addReason("缺少英语信息");
        }
        if (missPenalty > 0) acc.addScore(-missPenalty);
    }

    @Override
    public String name() { return "MissingInfoPenaltyPolicy"; }
}
