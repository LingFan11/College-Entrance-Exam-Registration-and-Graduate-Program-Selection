package com.lingfan.xspp.grad.repository;

import com.lingfan.xspp.grad.entity.GradSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.time.LocalDateTime;

public interface GradSessionRepository extends JpaRepository<GradSession, String> {
    Optional<GradSession> findBySessionId(String sessionId);

    @Modifying
    @Transactional
    @Query("delete from GradSession s where s.lastSeen < :deadline")
    int deleteOlderThan(@Param("deadline") LocalDateTime deadline);
}
