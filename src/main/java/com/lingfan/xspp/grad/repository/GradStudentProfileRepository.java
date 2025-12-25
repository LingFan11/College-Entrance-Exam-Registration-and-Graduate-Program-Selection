package com.lingfan.xspp.grad.repository;

import com.lingfan.xspp.grad.entity.GradStudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GradStudentProfileRepository extends JpaRepository<GradStudentProfile, Long> {
    Optional<GradStudentProfile> findByUserId(Long userId);
}
