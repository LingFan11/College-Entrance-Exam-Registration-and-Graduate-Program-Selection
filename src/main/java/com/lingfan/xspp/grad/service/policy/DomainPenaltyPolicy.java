package com.lingfan.xspp.grad.service.policy;

import com.lingfan.xspp.grad.entity.GradMajorDomainMap;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class DomainPenaltyPolicy implements RecommendPolicy {
    @Override
    public void apply(PolicyContext ctx, ScoreAccumulator acc) {
        String major = ctx.getProfile().getCurrentMajor();
        String majorLower = major == null ? "" : major.toLowerCase(Locale.ROOT);
        String majorDomain = "OTHER";
        try {
            List<GradMajorDomainMap> maps = ctx.getMajorDomainMaps();
            if (maps != null) {
                for (GradMajorDomainMap mdm : maps) {
                    String kwd = mdm.getKeyword();
                    if (kwd == null || kwd.isEmpty()) continue;
                    if ((major != null && major.contains(kwd)) || majorLower.contains(kwd.toLowerCase(Locale.ROOT))) {
                        majorDomain = mdm.getDomainCode();
                        break;
                    }
                }
            }
        } catch (Exception ignored) {}
        if ("OTHER".equals(majorDomain)) {
            try {
                Map<String, List<String>> kw = ctx.getProps().getMajorDomainKeywords();
                for (Map.Entry<String, List<String>> en : kw.entrySet()) {
                    for (String key : en.getValue()) {
                        if ((major != null && major.contains(key)) || majorLower.contains(key.toLowerCase(Locale.ROOT))) {
                            majorDomain = en.getKey();
                            break;
                        }
                    }
                    if (!"OTHER".equals(majorDomain)) break;
                }
            } catch (Exception ignored) {}
        }
        // mentor domain: prefer directionDomain map; fallback to csRootIds via parent
        String mentorDomain = "OTHER";
        String mapped = null;
        Map<Long, String> dirDomain = ctx.getDirectionDomain();
        if (dirDomain != null) {
            for (Long md : ctx.getMentorDirections()) {
                String dc = dirDomain.get(md);
                if (dc != null) { mapped = dc; break; }
            }
        }
        if (mapped != null) {
            mentorDomain = mapped;
        } else {
            Set<Long> csRoots = Set.copyOf(ctx.getProps().getCsRootIds());
            for (Long md : ctx.getMentorDirections()) {
                Long p = ctx.getDirectionParent().get(md);
                Long root = (p != null ? p : md);
                if (root != null && csRoots.contains(root)) { mentorDomain = "CS"; break; }
            }
        }
        if (!"OTHER".equals(majorDomain) && !"OTHER".equals(mentorDomain) && !majorDomain.equals(mentorDomain)) {
            acc.addScore(-Math.abs(ctx.getProps().getDomainMismatchPenalty()));
            acc.setStatusAtLeast("borderline");
            acc.addReason("专业方向不匹配");
        }
    }

    @Override
    public String name() { return "DomainPenaltyPolicy"; }
}
