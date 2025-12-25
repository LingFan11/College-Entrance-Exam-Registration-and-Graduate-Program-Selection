package com.lingfan.xspp.grad.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@ConfigurationProperties(prefix = "grad.recommend")
public class GradRecommendProperties {
    /** Top-N directions to consider by default (cap) */
    private int topN = 3;

    /** Root direction IDs considered as CS domain (e.g., 101=计算机科学) */
    private List<Long> csRootIds = List.of(101L);

    /** Major domain keywords mapping for simple domain inference */
    private Map<String, List<String>> majorDomainKeywords = Map.of(
            "CS", List.of("计算机", "软件", "cs", "人工智能", "软件工程"),
            "LIT", List.of("汉语言", "中文", "文学")
    );

    /** Penalty when student major domain mismatches mentor domain */
    private int domainMismatchPenalty = 30;

    /** Base score to use when no target directions are provided */
    private int noPrefBaseScore = 20;

    /** Penalty when key profile info is missing (e.g., GPA/English/Major) */
    private int missingInfoPenalty = 5;

    /** Gate results by minimum score */
    private boolean useMinScoreGate = true;
    private double minScore = 60.0;

    /** Consider profile insufficient if missing fields >= threshold (among major/gpa/english/targetDirections) */
    private int insufficientProfileMissingFieldsThreshold = 1;

    /** Basic rate limit configs */
    private int rateLimitPerIpPerMinute = 60;
    private int rateLimitPerSessionPerMinute = 60;

    /** Rate limit Retry-After seconds and session TTL days (cleanup) */
    private int rateLimitRetryAfterSeconds = 30;
    private int sessionTtlDays = 7;

    public int getTopN() { return topN; }
    public void setTopN(int topN) { this.topN = topN; }

    public List<Long> getCsRootIds() { return csRootIds; }
    public void setCsRootIds(List<Long> csRootIds) { this.csRootIds = csRootIds; }

    public Map<String, List<String>> getMajorDomainKeywords() { return majorDomainKeywords; }
    public void setMajorDomainKeywords(Map<String, List<String>> majorDomainKeywords) { this.majorDomainKeywords = majorDomainKeywords; }

    public int getDomainMismatchPenalty() { return domainMismatchPenalty; }
    public void setDomainMismatchPenalty(int domainMismatchPenalty) { this.domainMismatchPenalty = domainMismatchPenalty; }

    public int getNoPrefBaseScore() { return noPrefBaseScore; }
    public void setNoPrefBaseScore(int noPrefBaseScore) { this.noPrefBaseScore = noPrefBaseScore; }

    public int getMissingInfoPenalty() { return missingInfoPenalty; }
    public void setMissingInfoPenalty(int missingInfoPenalty) { this.missingInfoPenalty = missingInfoPenalty; }

    public boolean isUseMinScoreGate() { return useMinScoreGate; }
    public void setUseMinScoreGate(boolean useMinScoreGate) { this.useMinScoreGate = useMinScoreGate; }

    public double getMinScore() { return minScore; }
    public void setMinScore(double minScore) { this.minScore = minScore; }

    public int getInsufficientProfileMissingFieldsThreshold() { return insufficientProfileMissingFieldsThreshold; }
    public void setInsufficientProfileMissingFieldsThreshold(int insufficientProfileMissingFieldsThreshold) { this.insufficientProfileMissingFieldsThreshold = insufficientProfileMissingFieldsThreshold; }

    public int getRateLimitPerIpPerMinute() { return rateLimitPerIpPerMinute; }
    public void setRateLimitPerIpPerMinute(int rateLimitPerIpPerMinute) { this.rateLimitPerIpPerMinute = rateLimitPerIpPerMinute; }

    public int getRateLimitPerSessionPerMinute() { return rateLimitPerSessionPerMinute; }
    public void setRateLimitPerSessionPerMinute(int rateLimitPerSessionPerMinute) { this.rateLimitPerSessionPerMinute = rateLimitPerSessionPerMinute; }

    public int getRateLimitRetryAfterSeconds() { return rateLimitRetryAfterSeconds; }
    public void setRateLimitRetryAfterSeconds(int rateLimitRetryAfterSeconds) { this.rateLimitRetryAfterSeconds = rateLimitRetryAfterSeconds; }

    public int getSessionTtlDays() { return sessionTtlDays; }
    public void setSessionTtlDays(int sessionTtlDays) { this.sessionTtlDays = sessionTtlDays; }
}
