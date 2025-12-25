package com.lingfan.xspp.grad.repository;

import com.lingfan.xspp.grad.entity.GradInstitutionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 机构分组仓库 (Requirements 4.2, 5.3)
 */
public interface GradInstitutionGroupRepository extends JpaRepository<GradInstitutionGroup, Long> {

    /**
     * 按机构代码查询所有分组，按最低分降序排列 (Requirements 5.3)
     */
    List<GradInstitutionGroup> findByInstitutionCodeOrderByMinTotalScoreDesc(String institutionCode);

    /**
     * 按机构代码查询所有分组
     */
    List<GradInstitutionGroup> findByInstitutionCode(String institutionCode);

    /**
     * 按分数范围查询匹配的分组 (Requirements 4.2)
     * 查找分数在 [minTotalScore, maxTotalScore] 范围内的分组
     */
    @Query("SELECT g FROM GradInstitutionGroup g WHERE g.institutionCode = :institutionCode " +
           "AND (g.minTotalScore IS NULL OR g.minTotalScore <= :score) " +
           "AND (g.maxTotalScore IS NULL OR g.maxTotalScore >= :score) " +
           "ORDER BY g.priority DESC, g.minTotalScore DESC")
    List<GradInstitutionGroup> findMatchingGroups(
            @Param("institutionCode") String institutionCode,
            @Param("score") Integer score);

    /**
     * 查找分数匹配的最高优先级分组 (Requirements 4.3)
     */
    @Query("SELECT g FROM GradInstitutionGroup g WHERE g.institutionCode = :institutionCode " +
           "AND (g.minTotalScore IS NULL OR g.minTotalScore <= :score) " +
           "AND (g.maxTotalScore IS NULL OR g.maxTotalScore >= :score) " +
           "ORDER BY g.priority DESC, g.minTotalScore DESC LIMIT 1")
    Optional<GradInstitutionGroup> findBestMatchingGroup(
            @Param("institutionCode") String institutionCode,
            @Param("score") Integer score);
}
