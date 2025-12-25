package com.lingfan.xspp.school.repo;

import com.lingfan.xspp.school.entity.SchUnivMajor;
import com.lingfan.xspp.school.entity.SchUnivMajor.UnivMajorId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchUnivMajorRepository extends JpaRepository<SchUnivMajor, UnivMajorId> {
    List<SchUnivMajor> findByUniversityId(Long universityId);
}
