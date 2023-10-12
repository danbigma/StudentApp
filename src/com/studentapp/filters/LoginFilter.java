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

@WebFilter(filterName = "/loginfilter", urlPatterns = { "/admin/*" })
public class LoginFilter implements Filter {
	private ServletContext context;

	public LoginFilter() {
	}

	public void destroy() {
	}

	private boolean isUserAdmin(HttpServletRequest request) {
	    HttpSession session = request.getSession(false);
	    if (session != null) {
	        String username = (String) session.getAttribute("username");
	        return "admin".equals(username);
	    }
	    return false;
	}

	private void redirectToLoginPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    this.context.log("Unauthorized access request");
	    response.sendRedirect(request.getContextPath() + "/login.jsp");
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	        throws IOException, ServletException {
	    HttpServletRequest httpRequest = (HttpServletRequest) request;
	    HttpServletResponse httpResponse = (HttpServletResponse) response;

	    if (isUserAdmin(httpRequest)) {
	        chain.doFilter(request, response);
	    } else {
	        redirectToLoginPage(httpRequest, httpResponse);
	    }
	}


	public void init(FilterConfig fConfig) throws ServletException {
		this.context = fConfig.getServletContext();
		this.context.log("AuthenticationFilter initialized");
		System.out.println("Login filter init!");
	}

}
