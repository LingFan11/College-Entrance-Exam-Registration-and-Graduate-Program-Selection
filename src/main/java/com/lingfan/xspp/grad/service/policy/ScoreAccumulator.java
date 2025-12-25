package com.lingfan.xspp.grad.service.policy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreAccumulator {
    private double score;
    private String status; // fit | borderline | unfit
    private final List<String> reasons = new ArrayList<>();

    public ScoreAccumulator(double baseScore) {
        this.score = baseScore;
        this.status = "fit";
    }

    public double getScore() { return score; }
    public String getStatus() { return status; }
    public List<String> getReasons() { return Collections.unmodifiableList(reasons); }

    public void addScore(double delta) {
        this.score += delta;
        if (this.score < 0) this.score = 0;
    }

    public void capScore(double max) {
        this.score = Math.min(this.score, max);
    }

    public void setStatusAtLeast(String newStatus) {
        // Order: fit < borderline < unfit
        int cur = order(this.status);
        int nxt = order(newStatus);
        if (nxt > cur) this.status = newStatus;
    }

    public void addReason(String reason) {
        if (reason != null && !reason.isBlank()) reasons.add(reason);
    }

    private int order(String s) {
        if ("unfit".equalsIgnoreCase(s)) return 3;
        if ("borderline".equalsIgnoreCase(s)) return 2;
        return 1; // fit
    }
}
