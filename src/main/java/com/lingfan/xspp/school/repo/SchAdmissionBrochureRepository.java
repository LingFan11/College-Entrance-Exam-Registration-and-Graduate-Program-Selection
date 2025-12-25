package com.lingfan.xspp.school.repo;

import com.lingfan.xspp.school.entity.SchAdmissionBrochure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchAdmissionBrochureRepository extends JpaRepository<SchAdmissionBrochure, Long> {
    List<SchAdmissionBrochure> findByUniversityIdAndDegreeAndYear(Long universityId, String degree, Integer year);
    List<SchAdmissionBrochure> findByUniversityIdAndDegree(Long universityId, String degree);
    List<SchAdmissionBrochure> findByUniversityId(Long universityId);
}
