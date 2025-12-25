package com.lingfan.xspp.grad.controller;

import com.lingfan.xspp.common.ApiResponse;
import com.lingfan.xspp.grad.dto.SessionInitResponse;
import com.lingfan.xspp.grad.entity.GradSession;
import com.lingfan.xspp.grad.entity.GradUser;
import com.lingfan.xspp.grad.repository.GradSessionRepository;
import com.lingfan.xspp.grad.repository.GradUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/grad/session")
public class GradSessionController {
    private final GradUserRepository userRepo;
    private final GradSessionRepository sessionRepo;

    public GradSessionController(GradUserRepository userRepo, GradSessionRepository sessionRepo) {
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepo;
    }

    @PostMapping("/init")
    @Transactional
    public ApiResponse<SessionInitResponse> init(HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        // Create user
        GradUser user = new GradUser();
        user = userRepo.save(user);
        // Create session
        String ip = clientIp(request);
        GradSession s = GradSession.create(ip, user.getId());
        sessionRepo.save(s);
        return ApiResponse.ok(new SessionInitResponse(user.getId(), s.getSessionId()), traceId);
    }

    private String clientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();
        return ip;
    }
}
