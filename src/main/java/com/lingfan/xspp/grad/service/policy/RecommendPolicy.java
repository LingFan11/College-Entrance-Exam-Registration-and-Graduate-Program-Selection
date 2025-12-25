package com.lingfan.xspp.grad.service.policy;

public interface RecommendPolicy {
    void apply(PolicyContext ctx, ScoreAccumulator acc);
    String name();
}
