package com.works.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class LogFilter extends OncePerRequestFilter {

    public static final String ATTR_USER = "log.username";
    public static final String ATTR_ROLES = "log.roles";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();
        Throwable error = null;

        try {
            XssRequestWrapper xssReq = new XssRequestWrapper(request);
            filterChain.doFilter(xssReq, response);
        } catch (IOException | ServletException | RuntimeException ex) {
            error = ex;
            throw ex;
        } finally {
            long duration = System.currentTimeMillis() - start;

            // Exception fırladıysa container 500 döndürecek
            int status = (error != null && response.getStatus() < 400)
                    ? 500 : response.getStatus();

            String username = attr(request, ATTR_USER, "Anonymous");
            String roles = attr(request, ATTR_ROLES, "");
            String ip = request.getRemoteAddr();
            String agent = request.getHeader("User-Agent");
            String sessionId = request.getSession(false) != null
                    ? request.getSession(false).getId() : "No Session";

            if (error != null) {
                log.error("HTTP EXCEPTION | User: {} | Method: {} | URI: {} | Status: {} | Duration: {} ms | IP: {} | Error: {}",
                        username, request.getMethod(), request.getRequestURI(),
                        status, duration, ip, error.getMessage(), error);
            } else if (status >= 400) {
                log.error("HTTP ERROR | User: {} | Method: {} | URI: {} | Status: {} | Duration: {} ms | IP: {} | SessionID: {} | Roles: {} | Agent: {}",
                        username, request.getMethod(), request.getRequestURI(),
                        status, duration, ip, sessionId, roles, agent);
            } else {
                log.info("HTTP REQUEST | User: {} | Method: {} | URI: {} | Status: {} | Duration: {} ms | IP: {} | SessionID: {} | Roles: {} | Agent: {}",
                        username, request.getMethod(), request.getRequestURI(),
                        status, duration, ip, sessionId, roles, agent);
            }
        }
    }

    private String attr(HttpServletRequest req, String name, String def) {
        Object v = req.getAttribute(name);
        return v != null ? v.toString() : def;
    }
}