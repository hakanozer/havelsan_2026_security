package com.works.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class FilterConfig implements Filter {

    private final RateLimiter rateLimiter;

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        // XSS koruması — request'i wrapper ile sar
        XssRequestWrapper xssReq = new XssRequestWrapper(req);

        String url = xssReq.getRequestURI();

        // 🔴 Rate limit kontrolü
        if (!rateLimiter.acquirePermission()) {
            log.warn("Rate limit exceeded for URL: {}", url);
            res.setStatus(429);
            res.setContentType("application/json");
            res.getWriter().write("""
                    {
                      "status": 429,
                      "error": "Too Many Requests",
                      "message": "Rate limit exceeded (max 5 requests per minute)"
                    }
                    """);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = "Anonymous";
        long time = System.currentTimeMillis();
        String agent = xssReq.getHeader("User-Agent");
        String ip = xssReq.getRemoteAddr();
        String sessionId = xssReq.getSession().getId();
        StringBuilder roles = new StringBuilder();

        if (auth != null) {
            username = auth.getName();
            for (GrantedAuthority authority : auth.getAuthorities()) {
                roles.append(authority.getAuthority()).append(" ");
            }
        }

        log.info("URL: {}, Username: {}, Roles: {}, Time: {}, Agent: {}, IP: {}, SessionId: {}",
                url, username, roles, time, agent, ip, sessionId);

        filterChain.doFilter(xssReq, res);  // wrapper ile devam et
    }
}