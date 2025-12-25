package com.lingfan.xspp.grad.repository;

import com.lingfan.xspp.grad.entity.GradMentorGroupMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 导师-分组关联仓库 (Requirements 4.4)
 */
public interface GradMentorGroupMapRepository extends JpaRepository<GradMentorGroupMap, GradMentorGroupMap.MentorGroupId> {

    /**
     * 按导师ID查询所属分组
     */
    List<GradMentorGroupMap> findByMentorId(Long mentorId);

    /**
     * 按分组ID查询所属导师
     */
    List<GradMentorGroupMap> findByGroupId(Long groupId);

    /**
     * 按分组ID列表查询所有导师ID
     */
    @Query("SELECT m.mentorId FROM GradMentorGroupMap m WHERE m.groupId IN :groupIds")
    List<Long> findMentorIdsByGroupIds(@Param("groupIds") List<Long> groupIds);

    /**
     * 按导师ID列表查询所有分组ID
     */
    @Query("SELECT m.groupId FROM GradMentorGroupMap m WHERE m.mentorId IN :mentorIds")
    List<Long> findGroupIdsByMentorIds(@Param("mentorIds") List<Long> mentorIds);

    /**
     * 检查导师是否属于指定分组
     */
    boolean existsByMentorIdAndGroupId(Long mentorId, Long groupId);

    /**
     * 删除导师的所有分组关联
     */
    void deleteByMentorId(Long mentorId);

    /**
     * 删除分组的所有导师关联
     */
    void deleteByGroupId(Long groupId);
}
