package com.studentapp.filters;

import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

//Implements Filter class
public class LogFilter implements Filter {
    private static final Logger logger = Logger.getLogger(LogFilter.class);
	public void init(FilterConfig config) throws ServletException {
		logger.info("LogFilter init!");
		// Get init parameter
		String testParam = config.getInitParameter("test-param");

		// Print the init parameter
		logger.info("Test Param: " + testParam);
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws java.io.IOException, ServletException {
		
        HttpServletRequest req = (HttpServletRequest) request;
        
        String servletPath = req.getServletPath();
 
        logger.info("#INFO " + new Date());
        logger.info("ServletPath: " + servletPath);
        logger.info("ContextPath " + req.getContextPath());
        logger.info("URL => " + req.getRequestURL());
        logger.info("QueryString => " + req.getQueryString());
        logger.info("---------------------------------------------------");

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
		logger.info("LogFilter destroy!");
	}
}
