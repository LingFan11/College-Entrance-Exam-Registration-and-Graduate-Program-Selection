package com.lingfan.xspp.grad.repository;

import com.lingfan.xspp.grad.entity.GradMentorPublication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradMentorPublicationRepository extends JpaRepository<GradMentorPublication, Long> {
    long countByMentorId(Long mentorId);
}
