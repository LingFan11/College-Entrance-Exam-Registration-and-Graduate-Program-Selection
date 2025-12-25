package com.lingfan.xspp.grad.property;

import com.lingfan.xspp.grad.dto.GroupMatchResponse;
import com.lingfan.xspp.grad.dto.InstitutionGroupDTO;
import com.lingfan.xspp.grad.entity.GradInstitutionGroup;
import com.lingfan.xspp.grad.repository.GradInstitutionGroupRepository;
import com.lingfan.xspp.grad.service.GradInstitutionGroupService;
import com.lingfan.xspp.grad.service.impl.GradInstitutionGroupServiceImpl;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * **Feature: grad-exam-score-requirements, Property 5: Institution Group Assignment**
 * **Validates: Requirements 4.2, 4.3, 4.4**
 * 
 * Property: For any set of institution groups with non-overlapping score ranges and any student
 * total score, the student SHALL be assigned to exactly one group whose range contains the score,
 * or to no group if the score falls outside all ranges. When ranges overlap, the group with
 * higher priority SHALL be selected.
 */
public class InstitutionGroupAssignmentPropertyTest {

    private static final String TEST_INSTITUTION = "CAS";

    /**
     * Property 5a: Score within range matches exactly one group
     * For any score that falls within a group's range, that group should be the best match.
     */
    @Property(tries = 100)
    void scoreWithinRange_matchesCorrectGroup(
            @ForAll @IntRange(min = 0, max = 500) Integer studentScore,
            @ForAll("nonOverlappingGroups") List<GroupConfig> groupConfigs) {

        Assume.that(!groupConfigs.isEmpty());

        // Create mock repository
        GradInstitutionGroupRepository mockRepo = mock(GradInstitutionGroupRepository.class);
        
        // Convert configs to entities
        List<GradInstitutionGroup> groups = createGroupEntities(groupConfigs);
        
        // Find expected matching group
        GroupConfig expectedMatch = findMatchingGroup(groupConfigs, studentScore);
        
        // Setup mock for findMatchingGroups
        List<GradInstitutionGroup> matchingGroups = groups.stream()
                .filter(g -> isScoreInRange(studentScore, g.getMinTotalScore(), g.getMaxTotalScore()))
                .sorted(Comparator.comparing(GradInstitutionGroup::getPriority).reversed()
                        .thenComparing(Comparator.comparing(GradInstitutionGroup::getMinTotalScore).reversed()))
                .collect(Collectors.toList());
        
        when(mockRepo.findMatchingGroups(eq(TEST_INSTITUTION), eq(studentScore)))
                .thenReturn(matchingGroups);
        when(mockRepo.findByInstitutionCodeOrderByMinTotalScoreDesc(eq(TEST_INSTITUTION)))
                .thenReturn(groups.stream()
                        .sorted(Comparator.comparing(GradInstitutionGroup::getMinTotalScore).reversed())
                        .collect(Collectors.toList()));

        GradInstitutionGroupService service = new GradInstitutionGroupServiceImpl(mockRepo);
        GroupMatchResponse result = service.matchGroup(TEST_INSTITUTION, studentScore);

        if (expectedMatch == null) {
            // Score outside all ranges - should have no match
            assert result.getMatchedGroup() == null :
                    String.format("Score %d outside all ranges should have no match, but got %s",
                            studentScore, result.getMatchedGroup() != null ? result.getMatchedGroup().getGroupName() : "null");
        } else {
            // Score within a range - should match the expected group
            assert result.getMatchedGroup() != null :
                    String.format("Score %d should match group %s but got no match",
                            studentScore, expectedMatch.groupName);
            assert expectedMatch.groupName.equals(result.getMatchedGroup().getGroupName()) :
                    String.format("Score %d should match group %s but matched %s",
                            studentScore, expectedMatch.groupName, result.getMatchedGroup().getGroupName());
        }
    }

    /**
     * Property 5b: Higher priority wins when ranges overlap
     * When a score falls within multiple overlapping ranges, the group with higher priority is selected.
     */
    @Property(tries = 100)
    void overlappingRanges_higherPriorityWins(
            @ForAll @IntRange(min = 100, max = 400) Integer studentScore,
            @ForAll @IntRange(min = 0, max = 10) Integer priority1,
            @ForAll @IntRange(min = 0, max = 10) Integer priority2) {

        Assume.that(!priority1.equals(priority2)); // Ensure different priorities

        // Create two overlapping groups
        GradInstitutionGroup group1 = createGroup(1L, "Group1", 100, 400, priority1);
        GradInstitutionGroup group2 = createGroup(2L, "Group2", 100, 400, priority2);

        GradInstitutionGroupRepository mockRepo = mock(GradInstitutionGroupRepository.class);

        // Both groups match, sorted by priority descending
        List<GradInstitutionGroup> matchingGroups = Arrays.asList(group1, group2).stream()
                .sorted(Comparator.comparing(GradInstitutionGroup::getPriority).reversed())
                .collect(Collectors.toList());

        when(mockRepo.findMatchingGroups(eq(TEST_INSTITUTION), eq(studentScore)))
                .thenReturn(matchingGroups);

        GradInstitutionGroupService service = new GradInstitutionGroupServiceImpl(mockRepo);
        GroupMatchResponse result = service.matchGroup(TEST_INSTITUTION, studentScore);

        // The group with higher priority should be the best match
        String expectedGroupName = priority1 > priority2 ? "Group1" : "Group2";
        assert result.getMatchedGroup() != null : "Should have a match";
        assert expectedGroupName.equals(result.getMatchedGroup().getGroupName()) :
                String.format("Expected %s (priority %d) but got %s",
                        expectedGroupName, Math.max(priority1, priority2),
                        result.getMatchedGroup().getGroupName());
    }

    /**
     * Property 5c: Score outside all ranges returns no match
     * Use a fixed group configuration with known gaps to ensure we can generate valid test cases.
     */
    @Property(tries = 100)
    void scoreOutsideAllRanges_returnsNoMatch(
            @ForAll("scoreInGap") Integer studentScore) {

        // Fixed groups with gaps: [0-100], [200-300], [400-500]
        // Gaps are: 101-199, 301-399
        List<GroupConfig> groupConfigs = Arrays.asList(
                new GroupConfig("Low", 0, 100, 0),
                new GroupConfig("Mid", 200, 300, 1),
                new GroupConfig("High", 400, 500, 2)
        );

        GradInstitutionGroupRepository mockRepo = mock(GradInstitutionGroupRepository.class);
        List<GradInstitutionGroup> groups = createGroupEntities(groupConfigs);

        when(mockRepo.findMatchingGroups(eq(TEST_INSTITUTION), eq(studentScore)))
                .thenReturn(Collections.emptyList());
        when(mockRepo.findByInstitutionCodeOrderByMinTotalScoreDesc(eq(TEST_INSTITUTION)))
                .thenReturn(groups);

        GradInstitutionGroupService service = new GradInstitutionGroupServiceImpl(mockRepo);
        GroupMatchResponse result = service.matchGroup(TEST_INSTITUTION, studentScore);

        assert result.getMatchedGroup() == null :
                String.format("Score %d outside all ranges should have no match", studentScore);
        assert result.getMessage() != null && !result.getMessage().isEmpty() :
                "Should have a message when no match found";
    }

    @Provide
    Arbitrary<Integer> scoreInGap() {
        // Generate scores that fall in the gaps: 101-199 or 301-399
        return Arbitraries.oneOf(
                Arbitraries.integers().between(101, 199),
                Arbitraries.integers().between(301, 399)
        );
    }

    /**
     * Property 5d: Null score returns no match with message
     */
    @Property(tries = 100)
    void nullScore_returnsNoMatchWithMessage(@ForAll("nonOverlappingGroups") List<GroupConfig> groupConfigs) {
        GradInstitutionGroupRepository mockRepo = mock(GradInstitutionGroupRepository.class);
        List<GradInstitutionGroup> groups = createGroupEntities(groupConfigs);

        when(mockRepo.findByInstitutionCodeOrderByMinTotalScoreDesc(eq(TEST_INSTITUTION)))
                .thenReturn(groups);

        GradInstitutionGroupService service = new GradInstitutionGroupServiceImpl(mockRepo);
        GroupMatchResponse result = service.matchGroup(TEST_INSTITUTION, null);

        assert result.getMatchedGroup() == null : "Null score should have no match";
        assert result.getMessage() != null && !result.getMessage().isEmpty() :
                "Should have a message when score is null";
    }

    // Helper methods and providers

    @Provide
    Arbitrary<List<GroupConfig>> nonOverlappingGroups() {
        return Arbitraries.integers().between(1, 5).flatMap(count -> {
            // Generate non-overlapping score ranges
            return Arbitraries.integers().between(0, 400).list().ofSize(count)
                    .map(starts -> {
                        List<GroupConfig> configs = new ArrayList<>();
                        List<Integer> sortedStarts = starts.stream().sorted().collect(Collectors.toList());
                        for (int i = 0; i < sortedStarts.size(); i++) {
                            int start = sortedStarts.get(i);
                            int end = (i < sortedStarts.size() - 1) 
                                    ? Math.min(start + 50, sortedStarts.get(i + 1) - 1)
                                    : Math.min(start + 50, 500);
                            if (end >= start) {
                                configs.add(new GroupConfig("Group" + i, start, end, i));
                            }
                        }
                        return configs;
                    });
        });
    }

    @Provide
    Arbitrary<List<GroupConfig>> disjointGroups() {
        // Generate groups with gaps between them
        return Arbitraries.of(
                Arrays.asList(
                        new GroupConfig("Low", 0, 200, 0),
                        new GroupConfig("High", 300, 500, 1)
                ),
                Arrays.asList(
                        new GroupConfig("VeryLow", 0, 100, 0),
                        new GroupConfig("Mid", 200, 300, 1),
                        new GroupConfig("VeryHigh", 400, 500, 2)
                )
        );
    }

    private List<GradInstitutionGroup> createGroupEntities(List<GroupConfig> configs) {
        List<GradInstitutionGroup> groups = new ArrayList<>();
        long id = 1L;
        for (GroupConfig config : configs) {
            groups.add(createGroup(id++, config.groupName, config.minScore, config.maxScore, config.priority));
        }
        return groups;
    }

    private GradInstitutionGroup createGroup(Long id, String name, Integer minScore, Integer maxScore, Integer priority) {
        GradInstitutionGroup group = new GradInstitutionGroup();
        group.setId(id);
        group.setInstitutionCode(TEST_INSTITUTION);
        group.setInstitutionName("中国科学院");
        group.setGroupName(name);
        group.setMinTotalScore(minScore);
        group.setMaxTotalScore(maxScore);
        group.setPriority(priority);
        return group;
    }

    private GroupConfig findMatchingGroup(List<GroupConfig> configs, Integer score) {
        return configs.stream()
                .filter(g -> score >= g.minScore && score <= g.maxScore)
                .max(Comparator.comparing(g -> g.priority))
                .orElse(null);
    }

    private boolean isScoreInRange(Integer score, Integer min, Integer max) {
        return (min == null || score >= min) && (max == null || score <= max);
    }

    static class GroupConfig {
        String groupName;
        int minScore;
        int maxScore;
        int priority;

        GroupConfig(String groupName, int minScore, int maxScore, int priority) {
            this.groupName = groupName;
            this.minScore = minScore;
            this.maxScore = maxScore;
            this.priority = priority;
        }
    }
}
