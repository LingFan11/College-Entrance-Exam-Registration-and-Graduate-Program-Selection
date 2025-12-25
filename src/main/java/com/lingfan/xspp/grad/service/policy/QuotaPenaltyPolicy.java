package com.lingfan.xspp.grad.service.policy;

import com.lingfan.xspp.grad.entity.GradMentorQuota;

public class QuotaPenaltyPolicy implements RecommendPolicy {
    @Override
    public void apply(PolicyContext ctx, ScoreAccumulator acc) {
        GradMentorQuota q = ctx.getMentorQuota();
        if (q != null && q.getTotalSlots() != null && q.getTotalSlots() > 0
                && q.getFilledSlots() != null && q.getFilledSlots() >= q.getTotalSlots()) {
            acc.addScore(-10.0);
            acc.setStatusAtLeast("borderline");
            acc.addReason("名额已满");
        }
    }

    @Override
    public String name() { return "QuotaPenaltyPolicy"; }
}
