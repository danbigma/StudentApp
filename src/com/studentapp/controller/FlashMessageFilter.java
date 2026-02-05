package com.studentapp.filters;

import com.studentapp.web.Web;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Moves flash messages from the Session to the Request scope after a redirect.
 * This allows the Post-Redirect-Get (PRG) pattern to work with user feedback.
 */
@WebFilter("/*")
public class FlashMessageFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession(false);

        if (session != null) {
            Object success = session.getAttribute(Web.Attrs.FLASH_SUCCESS);
            if (success != null) {
                req.setAttribute(Web.Attrs.FLASH_SUCCESS, success);
                session.removeAttribute(Web.Attrs.FLASH_SUCCESS);
            }

            Object error = session.getAttribute(Web.Attrs.FLASH_ERROR);
            if (error != null) {
                req.setAttribute(Web.Attrs.FLASH_ERROR, error);
                session.removeAttribute(Web.Attrs.FLASH_ERROR);
            }
        }
        chain.doFilter(request, response);
    }

    @Override public void init(FilterConfig filterConfig) { /* No-op */ }
    @Override public void destroy() { /* No-op */ }
}