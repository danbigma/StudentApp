package com.studentapp.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.studentapp.web.Web;

@WebFilter(filterName = "/loginfilter", urlPatterns = { "/admin/*" })
public class LoginFilter implements Filter {
	private ServletContext context;

	public LoginFilter() {
	}

	public void destroy() {
	}

    private boolean isUserLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object username = session.getAttribute(Web.Session.USERNAME);
        return username != null && String.valueOf(username).trim().length() > 0;
    }

	private void redirectToLoginPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    this.context.log("Unauthorized access request");
	    response.sendRedirect(request.getContextPath() + "/login.jsp");
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	        throws IOException, ServletException {
	    HttpServletRequest httpRequest = (HttpServletRequest) request;
	    HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (isUserLoggedIn(httpRequest)) {
            chain.doFilter(request, response);
        } else {
            redirectToLoginPage(httpRequest, httpResponse);
        }
    }

    public void init(FilterConfig fConfig) throws ServletException {
        this.context = fConfig.getServletContext();
        this.context.log("AuthenticationFilter initialized");
    }

}
