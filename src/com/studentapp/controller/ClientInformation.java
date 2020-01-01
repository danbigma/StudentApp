package com.studentapp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "clientInformation", urlPatterns = { "/clientInformation" })
public class ClientInformation extends HttpServlet {
	 private static final long serialVersionUID = 1L;
	 
	    public ClientInformation() {
	        super();
	    }
	 
	    @Override
	    protected void doGet(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
	    	
	    	List<String> infoList = new ArrayList<>();
	    	
	        ServletOutputStream out = response.getOutputStream();
	 
	        String requestURL = request.getRequestURL().toString();
	        String requestURI = request.getRequestURI();
	        String contextPath = request.getContextPath();
	        String servletPath = request.getServletPath();
	        String queryString = request.getQueryString();
	        String param1 = request.getParameter("text1");
	        String param2 = request.getParameter("text2");
	 
	        // Server Infos
	        String serverName = request.getServerName();
	        int serverPort = request.getServerPort();
	        
	        // Client Infos
	        String remoteAddr = request.getRemoteAddr();
	        String remoteHost = request.getRemoteHost();
	        String remoteUser = request.getRemoteUser();
	 
	        // Header Infos
	        Enumeration<String> headers = request.getHeaderNames();
	        while (headers.hasMoreElements()) {
	            String header = headers.nextElement();
	            out.println("<br><span>" + header + "</span>: " + request.getHeader(header));
	        }
	 
	        // Servlet Context info:
	        out.println("<br><br><b>Servlet Context info:</b>");
	        ServletContext servletContext = request.getServletContext();
	 
	        // Местоположение веб приложения на жестком диске (hard disk).
	        String realPath = servletContext.getRealPath("");

	    }
	 
	    @Override
	    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
	        this.doGet(request, response);
	    }

}
