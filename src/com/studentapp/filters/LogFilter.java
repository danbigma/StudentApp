package com.studentapp.filters;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        HttpServletResponse res = (HttpServletResponse) response;
        long startTime = System.currentTimeMillis();

        String queryString = req.getQueryString();
        String uri = req.getRequestURI() + (queryString == null ? "" : "?" + queryString);

        logger.info("--> " + req.getMethod() + " " + uri);

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logger.info("<-- " + req.getMethod() + " " + uri + " " + res.getStatus() + " " + duration + "ms");
        }
	}

	public void destroy() {
		/*
		 * Called before the Filter instance is removed from service by the web
		 * container
		 */
		logger.info("LogFilter destroy!");
	}
}
