package com.lingfan.xspp.grad.repository;

import com.lingfan.xspp.grad.entity.GradMajorDomainMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradMajorDomainMapRepository extends JpaRepository<GradMajorDomainMap, Long> {
    List<GradMajorDomainMap> findByDomainCode(String domainCode);
}
