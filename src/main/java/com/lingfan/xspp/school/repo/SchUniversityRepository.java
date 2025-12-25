package com.lingfan.xspp.school.repo;

import com.lingfan.xspp.school.entity.SchUniversity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SchUniversityRepository extends JpaRepository<SchUniversity, Long>, JpaSpecificationExecutor<SchUniversity> {
}
