package com.studentapp.filters;

import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

//Implements Filter class
public class LogFilter implements Filter {
	public void init(FilterConfig config) throws ServletException {
		System.out.println("LogFilter init!");
		// Get init parameter
		String testParam = config.getInitParameter("test-param");

		// Print the init parameter
		System.out.println("Test Param: " + testParam);
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws java.io.IOException, ServletException {
		
        HttpServletRequest req = (HttpServletRequest) request;
        
        String servletPath = req.getServletPath();
 
        System.out.println("#INFO " + new Date());
        System.out.println("ServletPath: " + servletPath);  
        System.out.println("ContextPath " + req.getContextPath());
        System.out.println("URL => " + req.getRequestURL());
        System.out.println("QueryString => " + req.getQueryString());
        System.out.println("---------------------------------------------------");

		// Get the IP address of client machine.
//		String ipAddress = request.getRemoteAddr();
//
//		// Log the IP address and current timestamp.
//		System.out.println("IP " + ipAddress + ", Time " + new Date().toString());
//		System.out.println("---------------------------------------------------");

		// Pass request back down the filter chain
		// Разрешить request продвигаться дальше. (Перейти данный Filter).
		chain.doFilter(request, response);
	}

	public void destroy() {
		/*
		 * Called before the Filter instance is removed from service by the web
		 * container
		 */
		System.out.println("LogFilter destroy!");
	}
}
