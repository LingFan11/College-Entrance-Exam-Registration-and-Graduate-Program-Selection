package com.lingfan.xspp.grad.repository;

import com.lingfan.xspp.grad.entity.GradMentorQuota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GradMentorQuotaRepository extends JpaRepository<GradMentorQuota, Long> {
    Optional<GradMentorQuota> findByMentorIdAndYear(Long mentorId, Integer year);
    List<GradMentorQuota> findByMentorIdInAndYear(Iterable<Long> mentorIds, Integer year);
    long countByMentorId(Long mentorId);
}
