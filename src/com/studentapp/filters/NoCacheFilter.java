package com.studentapp.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Prevents caching of protected content so the browser back button
 * cannot reveal stale pages after logout.
 */
@WebFilter(filterName = "NoCacheFilter", urlPatterns = { "/admin/*" })
public class NoCacheFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP/1.1
        resp.setHeader("Pragma", "no-cache"); // HTTP/1.0
        resp.setDateHeader("Expires", 0); // Proxies
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() { }
}

