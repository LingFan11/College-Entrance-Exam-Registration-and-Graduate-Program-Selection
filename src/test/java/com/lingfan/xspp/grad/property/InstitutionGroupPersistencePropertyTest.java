package com.lingfan.xspp.grad.property;

import com.lingfan.xspp.grad.dto.InstitutionGroupDTO;
import com.lingfan.xspp.grad.entity.GradInstitutionGroup;
import com.lingfan.xspp.grad.repository.GradInstitutionGroupRepository;
import com.lingfan.xspp.grad.service.GradInstitutionGroupService;
import com.lingfan.xspp.grad.service.impl.GradInstitutionGroupServiceImpl;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;
import net.jqwik.api.constraints.StringLength;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * **Feature: grad-exam-score-requirements, Property 6: Institution Group Persistence and Ordering**
 * **Validates: Requirements 5.1, 5.3**
 * 
 * Property: For any set of institution groups created for an institution, retrieving the groups
 * SHALL return them ordered by minTotalScore in descending order, with all fields (groupName,
 * institutionCode, minTotalScore, maxTotalScore) preserved.
 */
public class InstitutionGroupPersistencePropertyTest {

    private static final String TEST_INSTITUTION = "CAS";

    /**
     * Property 6a: Groups are returned ordered by minTotalScore descending
     */
    @Property(tries = 100)
    void groupsReturnedInDescendingMinScoreOrder(
            @ForAll("randomGroups") List<GroupData> groupDataList) {

        Assume.that(!groupDataList.isEmpty());

        GradInstitutionGroupRepository mockRepo = mock(GradInstitutionGroupRepository.class);
        
        // Create entities from group data
        List<GradInstitutionGroup> entities = createEntities(groupDataList);
        
        // Sort by minTotalScore descending (simulating repository behavior)
        List<GradInstitutionGroup> sortedEntities = entities.stream()
                .sorted(Comparator.comparing(
                        (GradInstitutionGroup g) -> g.getMinTotalScore() != null ? g.getMinTotalScore() : Integer.MIN_VALUE)
                        .reversed())
                .collect(Collectors.toList());

        when(mockRepo.findByInstitutionCodeOrderByMinTotalScoreDesc(eq(TEST_INSTITUTION)))
                .thenReturn(sortedEntities);

        GradInstitutionGroupService service = new GradInstitutionGroupServiceImpl(mockRepo);
        List<InstitutionGroupDTO> result = service.getGroupsByInstitution(TEST_INSTITUTION);

        // Verify ordering: each group's minTotalScore should be >= the next group's minTotalScore
        for (int i = 0; i < result.size() - 1; i++) {
            Integer current = result.get(i).getMinTotalScore();
            Integer next = result.get(i + 1).getMinTotalScore();
            
            // Handle nulls: null is treated as lowest
            int currentVal = current != null ? current : Integer.MIN_VALUE;
            int nextVal = next != null ? next : Integer.MIN_VALUE;
            
            assert currentVal >= nextVal :
                    String.format("Groups not in descending order: %s (min=%d) should come after %s (min=%d)",
                            result.get(i).getGroupName(), current,
                            result.get(i + 1).getGroupName(), next);
        }
    }

    /**
     * Property 6b: All fields are preserved through persistence
     */
    @Property(tries = 100)
    void allFieldsPreservedThroughPersistence(
            @ForAll @StringLength(min = 1, max = 50) String groupName,
            @ForAll @StringLength(min = 1, max = 10) String institutionCode,
            @ForAll("validMinScore") Integer minTotalScore,
            @ForAll("validMaxScore") Integer maxTotalScore,
            @ForAll @IntRange(min = 0, max = 10) Integer priority) {

        // Ensure valid range
        Assume.that(minTotalScore == null || maxTotalScore == null || minTotalScore <= maxTotalScore);

        GradInstitutionGroup entity = new GradInstitutionGroup();
        entity.setId(1L);
        entity.setGroupName(groupName);
        entity.setInstitutionCode(institutionCode);
        entity.setInstitutionName("Test Institution");
        entity.setMinTotalScore(minTotalScore);
        entity.setMaxTotalScore(maxTotalScore);
        entity.setPriority(priority);

        GradInstitutionGroupRepository mockRepo = mock(GradInstitutionGroupRepository.class);
        when(mockRepo.findById(1L)).thenReturn(Optional.of(entity));

        GradInstitutionGroupService service = new GradInstitutionGroupServiceImpl(mockRepo);
        InstitutionGroupDTO result = service.getGroupById(1L);

        assert result != null : "Group should be found";
        assert groupName.equals(result.getGroupName()) :
                String.format("groupName mismatch: expected '%s', got '%s'", groupName, result.getGroupName());
        assert institutionCode.equals(result.getInstitutionCode()) :
                String.format("institutionCode mismatch: expected '%s', got '%s'", institutionCode, result.getInstitutionCode());
        assertScoreEquals("minTotalScore", minTotalScore, result.getMinTotalScore());
        assertScoreEquals("maxTotalScore", maxTotalScore, result.getMaxTotalScore());
        assert priority.equals(result.getPriority()) :
                String.format("priority mismatch: expected %d, got %d", priority, result.getPriority());
    }

    /**
     * Property 6c: Group count is preserved
     */
    @Property(tries = 100)
    void groupCountPreserved(@ForAll("randomGroups") List<GroupData> groupDataList) {
        GradInstitutionGroupRepository mockRepo = mock(GradInstitutionGroupRepository.class);
        
        List<GradInstitutionGroup> entities = createEntities(groupDataList);
        List<GradInstitutionGroup> sortedEntities = entities.stream()
                .sorted(Comparator.comparing(
                        (GradInstitutionGroup g) -> g.getMinTotalScore() != null ? g.getMinTotalScore() : Integer.MIN_VALUE)
                        .reversed())
                .collect(Collectors.toList());

        when(mockRepo.findByInstitutionCodeOrderByMinTotalScoreDesc(eq(TEST_INSTITUTION)))
                .thenReturn(sortedEntities);

        GradInstitutionGroupService service = new GradInstitutionGroupServiceImpl(mockRepo);
        List<InstitutionGroupDTO> result = service.getGroupsByInstitution(TEST_INSTITUTION);

        assert result.size() == groupDataList.size() :
                String.format("Group count mismatch: expected %d, got %d", groupDataList.size(), result.size());
    }

    /**
     * Property 6d: Empty institution returns empty list
     */
    @Property(tries = 100)
    void emptyInstitutionReturnsEmptyList(@ForAll @StringLength(min = 1, max = 10) String institutionCode) {
        GradInstitutionGroupRepository mockRepo = mock(GradInstitutionGroupRepository.class);
        when(mockRepo.findByInstitutionCodeOrderByMinTotalScoreDesc(eq(institutionCode)))
                .thenReturn(Collections.emptyList());

        GradInstitutionGroupService service = new GradInstitutionGroupServiceImpl(mockRepo);
        List<InstitutionGroupDTO> result = service.getGroupsByInstitution(institutionCode);

        assert result != null : "Result should not be null";
        assert result.isEmpty() : "Result should be empty for institution with no groups";
    }

    // Helper methods and providers

    private void assertScoreEquals(String fieldName, Integer expected, Integer actual) {
        if (expected == null) {
            assert actual == null : String.format("%s should be null but was %d", fieldName, actual);
        } else {
            assert expected.equals(actual) : String.format("%s mismatch: expected %d, got %d", fieldName, expected, actual);
        }
    }

    private List<GradInstitutionGroup> createEntities(List<GroupData> dataList) {
        List<GradInstitutionGroup> entities = new ArrayList<>();
        long id = 1L;
        for (GroupData data : dataList) {
            GradInstitutionGroup entity = new GradInstitutionGroup();
            entity.setId(id++);
            entity.setGroupName(data.groupName);
            entity.setInstitutionCode(TEST_INSTITUTION);
            entity.setInstitutionName("中国科学院");
            entity.setMinTotalScore(data.minScore);
            entity.setMaxTotalScore(data.maxScore);
            entity.setPriority(data.priority);
            entities.add(entity);
        }
        return entities;
    }

    @Provide
    Arbitrary<List<GroupData>> randomGroups() {
        return Arbitraries.integers().between(0, 5).flatMap(count ->
                groupData().list().ofSize(count)
        );
    }

    @Provide
    Arbitrary<GroupData> groupData() {
        return Combinators.combine(
                Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20),
                Arbitraries.integers().between(0, 400).injectNull(0.1),
                Arbitraries.integers().between(100, 500).injectNull(0.1),
                Arbitraries.integers().between(0, 10)
        ).as((name, min, max, priority) -> {
            // Ensure min <= max when both are non-null
            if (min != null && max != null && min > max) {
                int temp = min;
                min = max;
                max = temp;
            }
            return new GroupData(name, min, max, priority);
        });
    }

    @Provide
    Arbitrary<Integer> validMinScore() {
        return Arbitraries.integers().between(0, 400).injectNull(0.2);
    }

    @Provide
    Arbitrary<Integer> validMaxScore() {
        return Arbitraries.integers().between(100, 500).injectNull(0.2);
    }

    static class GroupData {
        String groupName;
        Integer minScore;
        Integer maxScore;
        Integer priority;

        GroupData(String groupName, Integer minScore, Integer maxScore, Integer priority) {
            this.groupName = groupName;
            this.minScore = minScore;
            this.maxScore = maxScore;
            this.priority = priority;
        }
    }
}
