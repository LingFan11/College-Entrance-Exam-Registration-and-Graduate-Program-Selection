package com.lingfan.xspp.grad.task;

import com.lingfan.xspp.grad.config.GradRecommendProperties;
import com.lingfan.xspp.grad.repository.GradSessionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SessionCleanupTask {
    private final GradSessionRepository sessionRepo;
    private final GradRecommendProperties props;

    public SessionCleanupTask(GradSessionRepository sessionRepo, GradRecommendProperties props) {
        this.sessionRepo = sessionRepo;
        this.props = props;
    }

    @Scheduled(cron = "0 0 * * * *") // hourly
    public void cleanup() {
        LocalDateTime deadline = LocalDateTime.now().minusDays(Math.max(1, props.getSessionTtlDays()));
        try {
            int deleted = sessionRepo.deleteOlderThan(deadline);
            // optionally log deleted count
        } catch (Exception ignored) {
            // best-effort cleanup; ignore
        }
    }
}
