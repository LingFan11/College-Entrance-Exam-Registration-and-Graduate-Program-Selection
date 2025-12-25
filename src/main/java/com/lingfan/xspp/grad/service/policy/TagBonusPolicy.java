package com.lingfan.xspp.grad.service.policy;

public class TagBonusPolicy implements RecommendPolicy {
    @Override
    public void apply(PolicyContext ctx, ScoreAccumulator acc) {
        String style = ctx.getProfile().getStylePref();
        if (style != null && ctx.getMentorTags() != null) {
            for (String tag : ctx.getMentorTags()) {
                if (tag != null && tag.contains(style)) {
                    acc.addScore(5.0);
                    break;
                }
            }
        }
    }

    @Override
    public String name() { return "TagBonusPolicy"; }
}
