package com.lingfan.xspp.grad.service.policy;

import java.util.Map;
import java.util.Objects;

public class DirectionProximityPolicy implements RecommendPolicy {
    @Override
    public void apply(PolicyContext ctx, ScoreAccumulator acc) {
        if (ctx.isNoPref()) {
            // no target directions: use low base and mark borderline
            acc.addScore(ctx.getProps().getNoPrefBaseScore());
            acc.setStatusAtLeast("borderline");
            acc.addReason("未提供目标方向");
            return;
        }
        // base 50 + direction score up to 45
        double dirScore = 0.0;
        for (Map.Entry<Long, Double> e : ctx.getTargetDirectionWeights().entrySet()) {
            Long sid = e.getKey();
            Double w = e.getValue();
            if (w == null) continue;
            if (ctx.getMentorDirections().contains(sid)) {
                dirScore += w;
            } else {
                Long sdParent = ctx.getDirectionParent().get(sid);
                for (Long md : ctx.getMentorDirections()) {
                    Long mdParent = ctx.getDirectionParent().get(md);
                    if (Objects.equals(sdParent, md) || Objects.equals(mdParent, sid)) {
                        dirScore += 0.7 * w;
                        break;
                    }
                    if (sdParent != null && mdParent != null && Objects.equals(sdParent, mdParent)) {
                        dirScore += 0.5 * w;
                        break;
                    }
                }
            }
        }
        acc.addScore(50.0 + 45.0 * Math.min(dirScore, 1.0));
        if (dirScore < 0.3) {
            acc.setStatusAtLeast("borderline");
        }
    }

    @Override
    public String name() { return "DirectionProximityPolicy"; }
}
