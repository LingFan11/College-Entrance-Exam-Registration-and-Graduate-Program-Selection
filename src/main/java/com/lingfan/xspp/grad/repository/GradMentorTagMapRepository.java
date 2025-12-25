package com.lingfan.xspp.grad.repository;

import com.lingfan.xspp.grad.entity.GradMentorTagMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface GradMentorTagMapRepository extends JpaRepository<GradMentorTagMap, GradMentorTagMap.PK> {
    List<GradMentorTagMap> findByMentorIdIn(Collection<Long> mentorIds);
    long countByMentorId(Long mentorId);
}
