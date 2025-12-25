package com.lingfan.xspp.school.repo;

import com.lingfan.xspp.school.entity.SchTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchTagRepository extends JpaRepository<SchTag, Long> {
    List<SchTag> findByTypeAndNameContainingIgnoreCase(String type, String keyword);
    List<SchTag> findByType(String type);
}
