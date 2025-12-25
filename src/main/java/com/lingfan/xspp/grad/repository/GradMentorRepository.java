package com.lingfan.xspp.grad.repository;

import com.lingfan.xspp.grad.entity.GradMentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface GradMentorRepository extends JpaRepository<GradMentor, Long>, JpaSpecificationExecutor<GradMentor> {
    List<GradMentor> findByUniversityId(Long universityId);
    List<GradMentor> findByIdIn(Collection<Long> ids);
}
