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

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String username;
		
		HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        
        if (session != null) {
        	username = (String) session.getAttribute("username");
        } else {
        	username = "default";
        }
        
        if ("admin".equals(username)) {
        	chain.doFilter(request, response);
        }  else {
        	this.context.log("Unauthorized access request");
        	res.sendRedirect(req.getContextPath() + "/login.jsp");
        }
        
	}

	public void init(FilterConfig fConfig) throws ServletException {
        this.context = fConfig.getServletContext();
        this.context.log("AuthenticationFilter initialized");
		System.out.println("Login filter init!");
	}

}
