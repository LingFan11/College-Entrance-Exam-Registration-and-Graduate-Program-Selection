package com.lingfan.xspp.grad.property;

import com.lingfan.xspp.grad.entity.GradStudentProfile;
import com.lingfan.xspp.grad.repository.GradStudentProfileRepository;
import com.lingfan.xspp.grad.service.GradProfileService;
import com.lingfan.xspp.grad.service.impl.GradProfileServiceImpl;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * **Feature: grad-exam-score-requirements, Property 2: Profile Score Round-Trip**
 * **Validates: Requirements 1.3, 1.4**
 * 
 * Property: For any valid set of exam scores (total, politics, english, math, professional),
 * saving the scores to a student profile and then retrieving that profile SHALL return
 * the exact same score values.
 */
public class ProfileScoreRoundTripPropertyTest {

    private static final AtomicLong userIdCounter = new AtomicLong(100000);

    /**
     * Property 2: Profile Score Round-Trip
     * For any valid exam scores, the service correctly persists and retrieves all score fields.
     * 
     * This test verifies that the GradProfileServiceImpl.saveOrUpdate method correctly
     * copies all exam score fields when updating an existing profile.
     */
    @Property(tries = 100)
    void profileScoreRoundTrip_preservesAllScores(
            @ForAll @IntRange(min = 0, max = 500) Integer examTotalScore,
            @ForAll @IntRange(min = 0, max = 100) Integer politicsScore,
            @ForAll @IntRange(min = 0, max = 100) Integer englishExamScore,
            @ForAll @IntRange(min = 0, max = 150) Integer mathScore,
            @ForAll @IntRange(min = 0, max = 150) Integer professionalScore) {

        Long userId = userIdCounter.incrementAndGet();

        // Create a mock repository that simulates save/retrieve behavior
        GradStudentProfileRepository mockRepo = mock(GradStudentProfileRepository.class);
        
        // Holder for the saved profile
        final GradStudentProfile[] savedProfile = new GradStudentProfile[1];
        
        // Mock findByUserId to return empty (new profile case)
        when(mockRepo.findByUserId(userId)).thenReturn(Optional.empty());
        
        // Mock save to capture and return the saved profile
        when(mockRepo.save(any(GradStudentProfile.class))).thenAnswer(invocation -> {
            GradStudentProfile p = invocation.getArgument(0);
            // Simulate database save by copying to a new object
            GradStudentProfile saved = new GradStudentProfile();
            saved.setId(1L);
            saved.setUserId(p.getUserId());
            saved.setExamTotalScore(p.getExamTotalScore());
            saved.setPoliticsScore(p.getPoliticsScore());
            saved.setEnglishExamScore(p.getEnglishExamScore());
            saved.setMathScore(p.getMathScore());
            saved.setProfessionalScore(p.getProfessionalScore());
            saved.setTargetInstitutionGroupId(p.getTargetInstitutionGroupId());
            savedProfile[0] = saved;
            return saved;
        });

        // Create service with mock repository
        GradProfileService service = new GradProfileServiceImpl(mockRepo);

        // Create profile with exam scores
        GradStudentProfile profile = new GradStudentProfile();
        profile.setUserId(userId);
        profile.setExamTotalScore(examTotalScore);
        profile.setPoliticsScore(politicsScore);
        profile.setEnglishExamScore(englishExamScore);
        profile.setMathScore(mathScore);
        profile.setProfessionalScore(professionalScore);

        // Save the profile
        GradStudentProfile saved = service.saveOrUpdate(profile);

        // Verify round-trip consistency - the saved profile should have all scores preserved
        assert examTotalScore.equals(saved.getExamTotalScore()) :
            String.format("examTotalScore mismatch: input %d, saved %d", examTotalScore, saved.getExamTotalScore());
        assert politicsScore.equals(saved.getPoliticsScore()) :
            String.format("politicsScore mismatch: input %d, saved %d", politicsScore, saved.getPoliticsScore());
        assert englishExamScore.equals(saved.getEnglishExamScore()) :
            String.format("englishExamScore mismatch: input %d, saved %d", englishExamScore, saved.getEnglishExamScore());
        assert mathScore.equals(saved.getMathScore()) :
            String.format("mathScore mismatch: input %d, saved %d", mathScore, saved.getMathScore());
        assert professionalScore.equals(saved.getProfessionalScore()) :
            String.format("professionalScore mismatch: input %d, saved %d", professionalScore, saved.getProfessionalScore());
    }

    /**
     * Property 2b: Update preserves scores
     * When updating an existing profile, the new score values should be correctly persisted.
     */
    @Property(tries = 100)
    void profileScoreUpdate_preservesNewScores(
            @ForAll @IntRange(min = 0, max = 500) Integer initialTotal,
            @ForAll @IntRange(min = 0, max = 500) Integer updatedTotal,
            @ForAll @IntRange(min = 0, max = 100) Integer initialPolitics,
            @ForAll @IntRange(min = 0, max = 100) Integer updatedPolitics,
            @ForAll @IntRange(min = 0, max = 100) Integer initialEnglish,
            @ForAll @IntRange(min = 0, max = 100) Integer updatedEnglish,
            @ForAll @IntRange(min = 0, max = 150) Integer initialMath,
            @ForAll @IntRange(min = 0, max = 150) Integer updatedMath,
            @ForAll @IntRange(min = 0, max = 150) Integer initialProfessional,
            @ForAll @IntRange(min = 0, max = 150) Integer updatedProfessional) {

        Long userId = userIdCounter.incrementAndGet();

        // Create existing profile
        GradStudentProfile existingProfile = new GradStudentProfile();
        existingProfile.setId(1L);
        existingProfile.setUserId(userId);
        existingProfile.setExamTotalScore(initialTotal);
        existingProfile.setPoliticsScore(initialPolitics);
        existingProfile.setEnglishExamScore(initialEnglish);
        existingProfile.setMathScore(initialMath);
        existingProfile.setProfessionalScore(initialProfessional);

        // Create mock repository
        GradStudentProfileRepository mockRepo = mock(GradStudentProfileRepository.class);
        
        // Mock findByUserId to return existing profile
        when(mockRepo.findByUserId(userId)).thenReturn(Optional.of(existingProfile));
        
        // Mock save to return the updated profile
        when(mockRepo.save(any(GradStudentProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GradProfileService service = new GradProfileServiceImpl(mockRepo);

        // Create update request with new scores
        GradStudentProfile updateRequest = new GradStudentProfile();
        updateRequest.setUserId(userId);
        updateRequest.setExamTotalScore(updatedTotal);
        updateRequest.setPoliticsScore(updatedPolitics);
        updateRequest.setEnglishExamScore(updatedEnglish);
        updateRequest.setMathScore(updatedMath);
        updateRequest.setProfessionalScore(updatedProfessional);

        // Update the profile
        GradStudentProfile result = service.saveOrUpdate(updateRequest);

        // Verify the existing profile was updated with new values
        assert updatedTotal.equals(result.getExamTotalScore()) :
            String.format("After update, examTotalScore should be %d but was %d", updatedTotal, result.getExamTotalScore());
        assert updatedPolitics.equals(result.getPoliticsScore()) :
            String.format("After update, politicsScore should be %d but was %d", updatedPolitics, result.getPoliticsScore());
        assert updatedEnglish.equals(result.getEnglishExamScore()) :
            String.format("After update, englishExamScore should be %d but was %d", updatedEnglish, result.getEnglishExamScore());
        assert updatedMath.equals(result.getMathScore()) :
            String.format("After update, mathScore should be %d but was %d", updatedMath, result.getMathScore());
        assert updatedProfessional.equals(result.getProfessionalScore()) :
            String.format("After update, professionalScore should be %d but was %d", updatedProfessional, result.getProfessionalScore());
    }

    /**
     * Property 2c: Null scores round-trip
     * Null score values should be preserved through save/retrieve cycle.
     */
    @Property(tries = 100)
    void profileScoreRoundTrip_preservesNullScores(
            @ForAll("nullableScore500") Integer examTotalScore,
            @ForAll("nullableScore100") Integer politicsScore,
            @ForAll("nullableScore100") Integer englishExamScore,
            @ForAll("nullableScore150") Integer mathScore,
            @ForAll("nullableScore150") Integer professionalScore) {

        Long userId = userIdCounter.incrementAndGet();

        GradStudentProfileRepository mockRepo = mock(GradStudentProfileRepository.class);
        when(mockRepo.findByUserId(userId)).thenReturn(Optional.empty());
        when(mockRepo.save(any(GradStudentProfile.class))).thenAnswer(invocation -> {
            GradStudentProfile p = invocation.getArgument(0);
            GradStudentProfile saved = new GradStudentProfile();
            saved.setId(1L);
            saved.setUserId(p.getUserId());
            saved.setExamTotalScore(p.getExamTotalScore());
            saved.setPoliticsScore(p.getPoliticsScore());
            saved.setEnglishExamScore(p.getEnglishExamScore());
            saved.setMathScore(p.getMathScore());
            saved.setProfessionalScore(p.getProfessionalScore());
            return saved;
        });

        GradProfileService service = new GradProfileServiceImpl(mockRepo);

        GradStudentProfile profile = new GradStudentProfile();
        profile.setUserId(userId);
        profile.setExamTotalScore(examTotalScore);
        profile.setPoliticsScore(politicsScore);
        profile.setEnglishExamScore(englishExamScore);
        profile.setMathScore(mathScore);
        profile.setProfessionalScore(professionalScore);

        GradStudentProfile saved = service.saveOrUpdate(profile);

        // Both null and non-null values should be preserved
        assertScoreEquals("examTotalScore", examTotalScore, saved.getExamTotalScore());
        assertScoreEquals("politicsScore", politicsScore, saved.getPoliticsScore());
        assertScoreEquals("englishExamScore", englishExamScore, saved.getEnglishExamScore());
        assertScoreEquals("mathScore", mathScore, saved.getMathScore());
        assertScoreEquals("professionalScore", professionalScore, saved.getProfessionalScore());
    }

    private void assertScoreEquals(String fieldName, Integer expected, Integer actual) {
        if (expected == null) {
            assert actual == null : String.format("Null %s should be preserved, but got %d", fieldName, actual);
        } else {
            assert expected.equals(actual) : String.format("%s mismatch: expected %d, got %d", fieldName, expected, actual);
        }
    }

    @Provide
    Arbitrary<Integer> nullableScore500() {
        return Arbitraries.oneOf(
            Arbitraries.just(null),
            Arbitraries.integers().between(0, 500)
        );
    }

    @Provide
    Arbitrary<Integer> nullableScore100() {
        return Arbitraries.oneOf(
            Arbitraries.just(null),
            Arbitraries.integers().between(0, 100)
        );
    }

    @Provide
    Arbitrary<Integer> nullableScore150() {
        return Arbitraries.oneOf(
            Arbitraries.just(null),
            Arbitraries.integers().between(0, 150)
        );
    }
}
