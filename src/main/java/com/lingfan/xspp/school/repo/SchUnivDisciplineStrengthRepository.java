package com.lingfan.xspp.school.repo;

import com.lingfan.xspp.school.entity.SchUnivDisciplineStrength;
import com.lingfan.xspp.school.entity.SchUnivDisciplineStrength.StrengthId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchUnivDisciplineStrengthRepository extends JpaRepository<SchUnivDisciplineStrength, StrengthId> {
    List<SchUnivDisciplineStrength> findByUniversityIdAndYear(Long universityId, Integer year);
    List<SchUnivDisciplineStrength> findByUniversityId(Long universityId);
}
