package com.lingfan.xspp.grad.repo;

import com.lingfan.xspp.grad.entity.GradSession;
import com.lingfan.xspp.grad.repository.GradSessionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
public class GradSessionCleanupTests {

    @Autowired
    private GradSessionRepository sessionRepo;

    @Test
    @Transactional
    void deleteOlderThan_removesExpired_only() {
        // create recent session
        GradSession recent = new GradSession();
        recent.setSessionId("recent-session");
        recent.setUserId(100L);
        recent.setIp("127.0.0.1");
        recent.setCreatedAt(LocalDateTime.now());
        recent.setLastSeen(LocalDateTime.now());
        sessionRepo.save(recent);

        // create expired session
        GradSession expired = new GradSession();
        expired.setSessionId("expired-session");
        expired.setUserId(101L);
        expired.setIp("127.0.0.2");
        expired.setCreatedAt(LocalDateTime.now().minusDays(10));
        expired.setLastSeen(LocalDateTime.now().minusDays(10));
        sessionRepo.save(expired);

        // delete sessions older than 7 days
        int deleted = sessionRepo.deleteOlderThan(LocalDateTime.now().minusDays(7));
        Assertions.assertEquals(1, deleted);

        // verify leftover
        Assertions.assertTrue(sessionRepo.findBySessionId("recent-session").isPresent());
        Assertions.assertTrue(sessionRepo.findBySessionId("expired-session").isEmpty());
    }
}
