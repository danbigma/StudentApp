package com.studentapp.filters;

import com.studentapp.config.AppConfig;
import com.studentapp.web.Web;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

public class CsrfFilter implements Filter {

    private static final SecureRandom RANDOM = new SecureRandom();
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
        if (!cfg.isCsrfEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(true);
        String token = (String) session.getAttribute(Web.Session.CSRF_TOKEN);
        if (token == null) {
            token = generateToken();
            session.setAttribute(Web.Session.CSRF_TOKEN, token);
        }
        // expose to views
        req.setAttribute(Web.Attrs.CSRF_TOKEN, token);

        String method = req.getMethod();
        boolean safe = "GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method);
        if (safe) {
            chain.doFilter(request, response);
            return;
        }

        String provided = req.getHeader("X-CSRF-Token");
        if (provided == null || provided.isEmpty()) {
            provided = req.getParameter(Web.Params.CSRF);
        }
        if (provided == null || !provided.equals(token)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() { }

    private String generateToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

