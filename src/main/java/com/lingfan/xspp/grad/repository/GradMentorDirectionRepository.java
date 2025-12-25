package com.lingfan.xspp.grad.repository;

import com.lingfan.xspp.grad.entity.GradMentorDirection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface GradMentorDirectionRepository extends JpaRepository<GradMentorDirection, GradMentorDirection.PK> {
    List<GradMentorDirection> findByDirectionId(Long directionId);
    List<GradMentorDirection> findByMentorIdIn(Collection<Long> mentorIds);
    List<GradMentorDirection> findByMentorId(Long mentorId);
    long countByMentorId(Long mentorId);
}
