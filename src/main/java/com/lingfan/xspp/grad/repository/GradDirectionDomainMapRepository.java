package com.lingfan.xspp.grad.repository;

import com.lingfan.xspp.grad.entity.GradDirectionDomainMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradDirectionDomainMapRepository extends JpaRepository<GradDirectionDomainMap, Long> {
    List<GradDirectionDomainMap> findByDirectionIdIn(Iterable<Long> ids);
}
