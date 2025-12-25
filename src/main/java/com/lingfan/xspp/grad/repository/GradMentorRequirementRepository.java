package com.lingfan.xspp.grad.repository;

import com.lingfan.xspp.grad.entity.GradMentorRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GradMentorRequirementRepository extends JpaRepository<GradMentorRequirement, Long> {
    Optional<GradMentorRequirement> findByMentorId(Long mentorId);
    List<GradMentorRequirement> findByMentorIdIn(Collection<Long> mentorIds);
}
