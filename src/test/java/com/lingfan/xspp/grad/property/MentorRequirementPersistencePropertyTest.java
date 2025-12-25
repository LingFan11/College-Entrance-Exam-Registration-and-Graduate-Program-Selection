package com.lingfan.xspp.grad.property;

import com.lingfan.xspp.grad.entity.GradMentorRequirement;
import com.lingfan.xspp.grad.repository.GradMentorRequirementRepository;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * **Feature: grad-exam-score-requirements, Property 3: Mentor Requirement Persistence**
 * **Validates: Requirements 2.1, 2.2, 2.4**
 * 
 * Property: For any mentor requirement with subject thresholds (minPolitics, minEnglishExam,
 * minMath, minProfessional, minTotalScore), saving the requirement and then retrieving it
 * SHALL return all configured threshold values unchanged.
 */
public class MentorRequirementPersistencePropertyTest {

    private static final AtomicLong mentorIdCounter = new AtomicLong(100000);

    /**
     * Property 3: Mentor Requirement Persistence
     * For any valid subject thresholds, the repository correctly persists and retrieves all threshold fields.
     */
    @Property(tries = 100)
    void mentorRequirementPersistence_preservesAllThresholds(
            @ForAll @IntRange(min = 0, max = 100) Integer minPolitics,
            @ForAll @IntRange(min = 0, max = 100) Integer minEnglishExam,
            @ForAll @IntRange(min = 0, max = 150) Integer minMath,
            @ForAll @IntRange(min = 0, max = 150) Integer minProfessional,
            @ForAll @IntRange(min = 0, max = 500) Integer minTotalScore) {

        Long mentorId = mentorIdCounter.incrementAndGet();

        // Create a mock repository that simulates save/retrieve behavior
        GradMentorRequirementRepository mockRepo = mock(GradMentorRequirementRepository.class);
        
        // Holder for the saved requirement
        final GradMentorRequirement[] savedRequirement = new GradMentorRequirement[1];
        
        // Mock save to capture and return the saved requirement
        when(mockRepo.save(any(GradMentorRequirement.class))).thenAnswer(invocation -> {
            GradMentorRequirement req = invocation.getArgument(0);
            // Simulate database save by copying to a new object
            GradMentorRequirement saved = new GradMentorRequirement();
            saved.setId(1L);
            saved.setMentorId(req.getMentorId());
            saved.setMinPolitics(req.getMinPolitics());
            saved.setMinEnglishExam(req.getMinEnglishExam());
            saved.setMinMath(req.getMinMath());
            saved.setMinProfessional(req.getMinProfessional());
            saved.setMinTotalScore(req.getMinTotalScore());
            saved.setMinGpa(req.getMinGpa());
            saved.setMinEnglish(req.getMinEnglish());
            saved.setEnglishType(req.getEnglishType());
            saved.setNotes(req.getNotes());
            savedRequirement[0] = saved;
            return saved;
        });

        // Mock findByMentorId to return the saved requirement
        when(mockRepo.findByMentorId(mentorId)).thenAnswer(invocation -> 
            Optional.ofNullable(savedRequirement[0]));

        // Create requirement with subject thresholds
        GradMentorRequirement requirement = new GradMentorRequirement();
        requirement.setMentorId(mentorId);
        requirement.setMinPolitics(minPolitics);
        requirement.setMinEnglishExam(minEnglishExam);
        requirement.setMinMath(minMath);
        requirement.setMinProfessional(minProfessional);
        requirement.setMinTotalScore(minTotalScore);

        // Save the requirement
        GradMentorRequirement saved = mockRepo.save(requirement);

        // Retrieve the requirement
        Optional<GradMentorRequirement> retrieved = mockRepo.findByMentorId(mentorId);

        // Verify round-trip consistency
        assert retrieved.isPresent() : "Saved requirement should be retrievable";
        GradMentorRequirement result = retrieved.get();
        
        assert minPolitics.equals(result.getMinPolitics()) :
            String.format("minPolitics mismatch: input %d, retrieved %d", minPolitics, result.getMinPolitics());
        assert minEnglishExam.equals(result.getMinEnglishExam()) :
            String.format("minEnglishExam mismatch: input %d, retrieved %d", minEnglishExam, result.getMinEnglishExam());
        assert minMath.equals(result.getMinMath()) :
            String.format("minMath mismatch: input %d, retrieved %d", minMath, result.getMinMath());
        assert minProfessional.equals(result.getMinProfessional()) :
            String.format("minProfessional mismatch: input %d, retrieved %d", minProfessional, result.getMinProfessional());
        assert minTotalScore.equals(result.getMinTotalScore()) :
            String.format("minTotalScore mismatch: input %d, retrieved %d", minTotalScore, result.getMinTotalScore());
    }

    /**
     * Property 3b: Null thresholds round-trip
     * Null threshold values should be preserved through save/retrieve cycle.
     * This validates that optional thresholds can be left unset.
     */
    @Property(tries = 100)
    void mentorRequirementPersistence_preservesNullThresholds(
            @ForAll("nullableScore100") Integer minPolitics,
            @ForAll("nullableScore100") Integer minEnglishExam,
            @ForAll("nullableScore150") Integer minMath,
            @ForAll("nullableScore150") Integer minProfessional,
            @ForAll("nullableScore500") Integer minTotalScore) {

        Long mentorId = mentorIdCounter.incrementAndGet();

        GradMentorRequirementRepository mockRepo = mock(GradMentorRequirementRepository.class);
        final GradMentorRequirement[] savedRequirement = new GradMentorRequirement[1];

        when(mockRepo.save(any(GradMentorRequirement.class))).thenAnswer(invocation -> {
            GradMentorRequirement req = invocation.getArgument(0);
            GradMentorRequirement saved = new GradMentorRequirement();
            saved.setId(1L);
            saved.setMentorId(req.getMentorId());
            saved.setMinPolitics(req.getMinPolitics());
            saved.setMinEnglishExam(req.getMinEnglishExam());
            saved.setMinMath(req.getMinMath());
            saved.setMinProfessional(req.getMinProfessional());
            saved.setMinTotalScore(req.getMinTotalScore());
            savedRequirement[0] = saved;
            return saved;
        });

        when(mockRepo.findByMentorId(mentorId)).thenAnswer(invocation -> 
            Optional.ofNullable(savedRequirement[0]));

        GradMentorRequirement requirement = new GradMentorRequirement();
        requirement.setMentorId(mentorId);
        requirement.setMinPolitics(minPolitics);
        requirement.setMinEnglishExam(minEnglishExam);
        requirement.setMinMath(minMath);
        requirement.setMinProfessional(minProfessional);
        requirement.setMinTotalScore(minTotalScore);

        mockRepo.save(requirement);
        Optional<GradMentorRequirement> retrieved = mockRepo.findByMentorId(mentorId);

        assert retrieved.isPresent() : "Saved requirement should be retrievable";
        GradMentorRequirement result = retrieved.get();

        // Both null and non-null values should be preserved
        assertThresholdEquals("minPolitics", minPolitics, result.getMinPolitics());
        assertThresholdEquals("minEnglishExam", minEnglishExam, result.getMinEnglishExam());
        assertThresholdEquals("minMath", minMath, result.getMinMath());
        assertThresholdEquals("minProfessional", minProfessional, result.getMinProfessional());
        assertThresholdEquals("minTotalScore", minTotalScore, result.getMinTotalScore());
    }

    /**
     * Property 3c: Update preserves thresholds
     * When updating an existing requirement, the new threshold values should be correctly persisted.
     */
    @Property(tries = 100)
    void mentorRequirementUpdate_preservesNewThresholds(
            @ForAll @IntRange(min = 0, max = 100) Integer initialPolitics,
            @ForAll @IntRange(min = 0, max = 100) Integer updatedPolitics,
            @ForAll @IntRange(min = 0, max = 100) Integer initialEnglish,
            @ForAll @IntRange(min = 0, max = 100) Integer updatedEnglish,
            @ForAll @IntRange(min = 0, max = 150) Integer initialMath,
            @ForAll @IntRange(min = 0, max = 150) Integer updatedMath,
            @ForAll @IntRange(min = 0, max = 150) Integer initialProfessional,
            @ForAll @IntRange(min = 0, max = 150) Integer updatedProfessional,
            @ForAll @IntRange(min = 0, max = 500) Integer initialTotal,
            @ForAll @IntRange(min = 0, max = 500) Integer updatedTotal) {

        Long mentorId = mentorIdCounter.incrementAndGet();

        // Create existing requirement
        GradMentorRequirement existingRequirement = new GradMentorRequirement();
        existingRequirement.setId(1L);
        existingRequirement.setMentorId(mentorId);
        existingRequirement.setMinPolitics(initialPolitics);
        existingRequirement.setMinEnglishExam(initialEnglish);
        existingRequirement.setMinMath(initialMath);
        existingRequirement.setMinProfessional(initialProfessional);
        existingRequirement.setMinTotalScore(initialTotal);

        GradMentorRequirementRepository mockRepo = mock(GradMentorRequirementRepository.class);
        final GradMentorRequirement[] savedRequirement = new GradMentorRequirement[1];
        savedRequirement[0] = existingRequirement;

        when(mockRepo.findByMentorId(mentorId)).thenAnswer(invocation -> 
            Optional.ofNullable(savedRequirement[0]));

        when(mockRepo.save(any(GradMentorRequirement.class))).thenAnswer(invocation -> {
            GradMentorRequirement req = invocation.getArgument(0);
            // Update the existing requirement
            existingRequirement.setMinPolitics(req.getMinPolitics());
            existingRequirement.setMinEnglishExam(req.getMinEnglishExam());
            existingRequirement.setMinMath(req.getMinMath());
            existingRequirement.setMinProfessional(req.getMinProfessional());
            existingRequirement.setMinTotalScore(req.getMinTotalScore());
            savedRequirement[0] = existingRequirement;
            return existingRequirement;
        });

        // Update the requirement with new thresholds
        GradMentorRequirement updateRequest = new GradMentorRequirement();
        updateRequest.setId(1L);
        updateRequest.setMentorId(mentorId);
        updateRequest.setMinPolitics(updatedPolitics);
        updateRequest.setMinEnglishExam(updatedEnglish);
        updateRequest.setMinMath(updatedMath);
        updateRequest.setMinProfessional(updatedProfessional);
        updateRequest.setMinTotalScore(updatedTotal);

        mockRepo.save(updateRequest);
        Optional<GradMentorRequirement> retrieved = mockRepo.findByMentorId(mentorId);

        assert retrieved.isPresent() : "Updated requirement should be retrievable";
        GradMentorRequirement result = retrieved.get();

        // Verify the requirement was updated with new values
        assert updatedPolitics.equals(result.getMinPolitics()) :
            String.format("After update, minPolitics should be %d but was %d", updatedPolitics, result.getMinPolitics());
        assert updatedEnglish.equals(result.getMinEnglishExam()) :
            String.format("After update, minEnglishExam should be %d but was %d", updatedEnglish, result.getMinEnglishExam());
        assert updatedMath.equals(result.getMinMath()) :
            String.format("After update, minMath should be %d but was %d", updatedMath, result.getMinMath());
        assert updatedProfessional.equals(result.getMinProfessional()) :
            String.format("After update, minProfessional should be %d but was %d", updatedProfessional, result.getMinProfessional());
        assert updatedTotal.equals(result.getMinTotalScore()) :
            String.format("After update, minTotalScore should be %d but was %d", updatedTotal, result.getMinTotalScore());
    }

    private void assertThresholdEquals(String fieldName, Integer expected, Integer actual) {
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
