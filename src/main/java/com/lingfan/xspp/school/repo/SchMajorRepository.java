package com.lingfan.xspp.school.repo;

import com.lingfan.xspp.school.entity.SchMajor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SchMajorRepository extends JpaRepository<SchMajor, Long> {
    List<SchMajor> findByIdIn(Collection<Long> ids);
}
