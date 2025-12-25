package com.lingfan.xspp.school.repo;

import com.lingfan.xspp.school.entity.SchDiscipline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SchDisciplineRepository extends JpaRepository<SchDiscipline, Long> {
    List<SchDiscipline> findByIdIn(Collection<Long> ids);
    List<SchDiscipline> findByParentId(Long parentId);
}
