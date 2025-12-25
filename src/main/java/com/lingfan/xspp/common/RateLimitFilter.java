package com.lingfan.xspp.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingfan.xspp.grad.config.GradRecommendProperties;
import com.lingfan.xspp.grad.repository.GradSessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(1)
public class RateLimitFilter implements Filter {

    private final GradRecommendProperties props;
    private final GradSessionRepository sessionRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    private static class Counter {
        AtomicInteger count = new AtomicInteger(0);
        long windowStartMs;
    }

    private final Map<String, Counter> ipCounters = new ConcurrentHashMap<>();
    private final Map<String, Counter> sessionCounters = new ConcurrentHashMap<>();

    public RateLimitFilter(GradRecommendProperties props, GradSessionRepository sessionRepo) {
        this.props = props;
        this.sessionRepo = sessionRepo;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI();
        // Allow session init and static resources without limit
        if (path.startsWith("/api/grad/session/init") || path.startsWith("/actuator") || path.startsWith("/static") ) {
            chain.doFilter(request, response);
            return;
        }
        // Only protect grad api
        if (!path.startsWith("/api/grad/")) {
            chain.doFilter(request, response);
            return;
        }
        String sessionId = req.getHeader("X-Session-Id");
        String clientIp = clientIp(req);

        boolean allowed;
        if (sessionId != null && !sessionId.isBlank()) {
            allowed = check(sessionCounters, sessionId, props.getRateLimitPerSessionPerMinute());
            // touch session lastSeen best-effort
            sessionRepo.findBySessionId(sessionId).ifPresent(s -> { s.setLastSeen(LocalDateTime.now()); sessionRepo.save(s); });
        } else {
            allowed = check(ipCounters, clientIp, props.getRateLimitPerIpPerMinute());
        }
        if (!allowed) {
            String traceId = UUID.randomUUID().toString();
            resp.setStatus(429);
            resp.setHeader("Retry-After", String.valueOf(props.getRateLimitRetryAfterSeconds()));
            resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
            resp.setContentType("application/json");
            ApiResponse<Void> body = ApiResponse.error("rate_limited", traceId);
            resp.getWriter().write(mapper.writeValueAsString(body));
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean check(Map<String, Counter> map, String key, int limitPerMinute) {
        long now = System.currentTimeMillis();
        long window = now / 60000L; // minute window key via ms bucket
        Counter c = map.computeIfAbsent(key, k -> {
            Counter nc = new Counter();
            nc.windowStartMs = window;
            return nc;
        });
        synchronized (c) {
            if (c.windowStartMs != window) {
                c.windowStartMs = window;
                c.count.set(0);
            }
            int cur = c.count.incrementAndGet();
            return cur <= Math.max(1, limitPerMinute);
        }
    }

    private String clientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();
        return ip == null ? "UNKNOWN" : ip;
    }
}
