package com.lingfan.xspp.grad.service.policy;

public class MinScoreGatePolicy implements RecommendPolicy {
    @Override
    public void apply(PolicyContext ctx, ScoreAccumulator acc) {
        // No-op here; gating is applied at aggregation stage to keep context
        // Implemented for completeness and future pipeline-wide coordination.
    }

    @Override
    public String name() { return "MinScoreGatePolicy"; }
}
