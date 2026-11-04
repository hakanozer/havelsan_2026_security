// RateLimitFilter.java
package com.works.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RateLimitFilter extends OncePerRequestFilter {

    private static final String PRODUCT_PATH = "/product/";
    private static final String NOTE_PATH = "/note/";

    private final RateLimiterRegistry registry;
    private final ConcurrentMap<String, RateLimiter> rateLimiters =
            new ConcurrentHashMap<>();

    public RateLimitFilter(RateLimiterRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        boolean authenticated = authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());

        String bucket;
        int limit;

        if (path.startsWith(PRODUCT_PATH)) {
            bucket = "product";
            limit = 10;
        } else if (path.startsWith(NOTE_PATH)) {
            bucket = "note";
            limit = 7;
        } else if (!authenticated) {
            bucket = "anonymous";
            limit = 3;
        } else {
            filterChain.doFilter(request, response);
            return;
        }

        String identity = authenticated
                ? authentication.getName()
                : getClientIp(request);

        String limiterKey = bucket + ":" + identity;
        RateLimiter limiter = rateLimiters.computeIfAbsent(
                limiterKey,
                key -> registry.rateLimiter(
                        key,
                        RateLimiterConfig.custom()
                                .limitRefreshPeriod(Duration.ofMinutes(1))
                                .limitForPeriod(limit)
                                .timeoutDuration(Duration.ZERO)
                                .build()
                )
        );

        if (!limiter.acquirePermission()) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("""
                    {
                      "status": 429,
                      "error": "Too Many Requests",
                      "message": "Rate limit exceeded"
                    }
                    """);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}