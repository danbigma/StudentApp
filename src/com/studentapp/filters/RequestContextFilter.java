package com.studentapp.filters;

import com.studentapp.config.AppConfig;
import org.apache.log4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RequestContextFilter implements Filter {

    private AppConfig cfg;

    @Override
    public void init(FilterConfig filterConfig) {
        this.cfg = AppConfig.get();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Correlation ID
        String reqId = headerOrGenerate(req, "X-Request-Id");
        resp.setHeader("X-Request-Id", reqId);
        MDC.put("requestId", reqId);

        try {
            // Security headers
            if (cfg.isSecurityHeadersEnabled()) {
                applySecurityHeaders(resp);
            }

            // CORS
            if (cfg.isCorsEnabled()) {
                if (handleCors(req, resp)) {
                    return; // preflight handled
                }
            }

            chain.doFilter(request, response);
        } finally {
            MDC.remove("requestId");
        }
    }

    @Override
    public void destroy() { }

    private String headerOrGenerate(HttpServletRequest req, String name) {
        String v = req.getHeader(name);
        return v == null || v.trim().isEmpty() ? UUID.randomUUID().toString() : v;
    }

    private void applySecurityHeaders(HttpServletResponse resp) {
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("X-Frame-Options", cfg.getFrameOptions());
        resp.setHeader("X-XSS-Protection", "1; mode=block");
        resp.setHeader("Referrer-Policy", cfg.getReferrerPolicy());
        String csp = cfg.getContentSecurityPolicy();
        if (csp != null && !csp.trim().isEmpty()) {
            resp.setHeader("Content-Security-Policy", csp);
        }
        // A modern alternative for X-XSS-Protection is CSP; kept for legacy browsers.
    }

    private boolean handleCors(HttpServletRequest req, HttpServletResponse resp) {
        String origin = req.getHeader("Origin");
        if (origin == null || origin.isEmpty()) {
            return false; // Not a CORS request
        }

        String allowed = cfg.getCorsAllowedOrigins();
        boolean any = "*".equals(allowed);
        boolean match = any || isOriginAllowed(origin, allowed);
        if (!match) {
            return false;
        }

        resp.setHeader("Vary", "Origin");
        resp.setHeader("Access-Control-Allow-Origin", any ? "*" : origin);
        if (cfg.isCorsAllowCredentials() && !any) {
            resp.setHeader("Access-Control-Allow-Credentials", "true");
        }
        resp.setHeader("Access-Control-Allow-Methods", cfg.getCorsAllowedMethods());
        resp.setHeader("Access-Control-Allow-Headers", cfg.getCorsAllowedHeaders());
        resp.setHeader("Access-Control-Max-Age", String.valueOf(cfg.getCorsMaxAgeSeconds()));

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return true;
        }
        return false;
    }

    private boolean isOriginAllowed(String origin, String allowedList) {
        Set<String> set = new HashSet<>();
        for (String s : Arrays.asList(allowedList.split(","))) {
            if (!s.trim().isEmpty()) set.add(s.trim());
        }
        return set.contains(origin);
    }
}
