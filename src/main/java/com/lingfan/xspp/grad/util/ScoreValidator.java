package com.lingfan.xspp.grad.util;

/**
 * 分数验证工具类
 * 用于验证考研初试成绩的有效性
 * Requirements: 1.1, 1.2, 1.5, 2.3, 5.2
 */
public class ScoreValidator {

    public enum ScoreType {
        TOTAL(0, 500),           // 总分 0-500
        POLITICS(0, 100),        // 政治 0-100
        ENGLISH(0, 100),         // 英语 0-100
        MATH(0, 150),            // 数学 0-150
        PROFESSIONAL(0, 150);    // 专业课 0-150

        private final int min;
        private final int max;

        ScoreType(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int getMin() { return min; }
        public int getMax() { return max; }
    }

    /**
     * 验证分数是否在有效范围内
     * @param score 分数值
     * @param type 分数类型
     * @return true 如果分数有效，false 否则
     */
    public static boolean isValid(Integer score, ScoreType type) {
        if (score == null) {
            return true; // null 值被视为有效（可选字段）
        }
        return score >= type.getMin() && score <= type.getMax();
    }

    /**
     * 验证分数范围是否有效（用于机构分组配置）
     * @param minScore 最低分
     * @param maxScore 最高分
     * @return true 如果范围有效（min <= max），false 否则
     */
    public static boolean isValidRange(Integer minScore, Integer maxScore) {
        if (minScore == null || maxScore == null) {
            return true; // null 值被视为有效
        }
        return minScore <= maxScore;
    }
}
