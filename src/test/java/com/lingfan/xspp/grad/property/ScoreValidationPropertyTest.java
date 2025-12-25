package com.lingfan.xspp.grad.property;

import com.lingfan.xspp.grad.util.ScoreValidator;
import com.lingfan.xspp.grad.util.ScoreValidator.ScoreType;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;

/**
 * **Feature: grad-exam-score-requirements, Property 1: Score Validation Consistency**
 * **Validates: Requirements 1.1, 1.2, 1.5, 2.3, 5.2**
 * 
 * Property: For any submitted score value and its corresponding score type,
 * the validation result SHALL be valid if and only if the score falls within
 * the defined range for that type.
 */
public class ScoreValidationPropertyTest {

    /**
     * Property 1a: Total score validation (0-500)
     * For any integer, isValid returns true iff score is in [0, 500]
     */
    @Property(tries = 100)
    void totalScoreValidation_isConsistent(@ForAll Integer score) {
        boolean expected = score >= 0 && score <= 500;
        boolean actual = ScoreValidator.isValid(score, ScoreType.TOTAL);
        assert expected == actual : 
            String.format("Total score %d: expected %s but got %s", score, expected, actual);
    }

    /**
     * Property 1b: Politics score validation (0-100)
     */
    @Property(tries = 100)
    void politicsScoreValidation_isConsistent(@ForAll Integer score) {
        boolean expected = score >= 0 && score <= 100;
        boolean actual = ScoreValidator.isValid(score, ScoreType.POLITICS);
        assert expected == actual : 
            String.format("Politics score %d: expected %s but got %s", score, expected, actual);
    }

    /**
     * Property 1c: English score validation (0-100)
     */
    @Property(tries = 100)
    void englishScoreValidation_isConsistent(@ForAll Integer score) {
        boolean expected = score >= 0 && score <= 100;
        boolean actual = ScoreValidator.isValid(score, ScoreType.ENGLISH);
        assert expected == actual : 
            String.format("English score %d: expected %s but got %s", score, expected, actual);
    }

    /**
     * Property 1d: Math score validation (0-150)
     */
    @Property(tries = 100)
    void mathScoreValidation_isConsistent(@ForAll Integer score) {
        boolean expected = score >= 0 && score <= 150;
        boolean actual = ScoreValidator.isValid(score, ScoreType.MATH);
        assert expected == actual : 
            String.format("Math score %d: expected %s but got %s", score, expected, actual);
    }

    /**
     * Property 1e: Professional score validation (0-150)
     */
    @Property(tries = 100)
    void professionalScoreValidation_isConsistent(@ForAll Integer score) {
        boolean expected = score >= 0 && score <= 150;
        boolean actual = ScoreValidator.isValid(score, ScoreType.PROFESSIONAL);
        assert expected == actual : 
            String.format("Professional score %d: expected %s but got %s", score, expected, actual);
    }

    /**
     * Property 1f: Score range validation (min <= max)
     * For institution group configuration
     */
    @Property(tries = 100)
    void scoreRangeValidation_isConsistent(
            @ForAll @IntRange(min = 0, max = 500) Integer minScore,
            @ForAll @IntRange(min = 0, max = 500) Integer maxScore) {
        boolean expected = minScore <= maxScore;
        boolean actual = ScoreValidator.isValidRange(minScore, maxScore);
        assert expected == actual : 
            String.format("Range [%d, %d]: expected %s but got %s", minScore, maxScore, expected, actual);
    }

    /**
     * Property 1g: Null scores are always valid (optional fields)
     */
    @Property(tries = 100)
    void nullScores_areAlwaysValid(@ForAll("scoreTypes") ScoreType type) {
        assert ScoreValidator.isValid(null, type) : 
            String.format("Null score for %s should be valid", type);
    }

    @Provide
    Arbitrary<ScoreType> scoreTypes() {
        return Arbitraries.of(ScoreType.values());
    }
}
