package com.studentapp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "clientInformation", urlPatterns = { "/admin/clientInformation" })
public class ClientInformation extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ClientInformation() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HashMap<String, String> infoList = new HashMap<>();

//	        ServletOutputStream out = response.getOutputStream();

		infoList.put("requestURL", request.getRequestURL().toString());
		infoList.put("requestURItrequestURI", request.getRequestURI());
		infoList.put("contextPath", request.getContextPath());
		infoList.put("servletPath", request.getServletPath());
		infoList.put("queryString", request.getQueryString());
		infoList.put("param1", request.getParameter("text1"));
		infoList.put("param2", request.getParameter("text2"));

		// Server Info
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
		
		infoList.put("serverName", serverName);
		infoList.put("serverPort", String.valueOf(serverPort));

		// Client Info
		String remoteAddr = request.getRemoteAddr();
		String remoteHost = request.getRemoteHost();
		String remoteUser = request.getRemoteUser();
		
		infoList.put("remoteAddr", remoteAddr);
		infoList.put("remoteHost", remoteHost);
		infoList.put("remoteUser", remoteUser);

		// Header Info
		Enumeration<String> headers = request.getHeaderNames();
		while (headers.hasMoreElements()) {
			String header = headers.nextElement();
			infoList.put(header + " ", request.getHeader(header));
		}

		// Servlet Context info:
		ServletContext servletContext = request.getServletContext();
		
		// Местоположение веб приложения на жестком диске (hard disk).
		String realPath = servletContext.getRealPath("/");
		infoList.put("realPath", realPath);

		
		// add infoList to the request
		request.setAttribute("infoList", infoList);
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/clientinformation.jsp");
		dispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

}
