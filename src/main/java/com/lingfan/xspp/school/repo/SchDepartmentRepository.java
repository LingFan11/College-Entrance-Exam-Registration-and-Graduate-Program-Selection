package com.lingfan.xspp.school.repo;

import com.lingfan.xspp.school.entity.SchDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchDepartmentRepository extends JpaRepository<SchDepartment, Long> {
    List<SchDepartment> findByUniversityId(Long universityId);
}
