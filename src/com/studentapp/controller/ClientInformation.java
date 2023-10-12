package com.studentapp.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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

		// Obtener información del cliente
		extractClientInfo(request, infoList);
		extractServerInfo(request, infoList);
		extractHeaderInfo(request, infoList);

		// Servlet Context info:
		ServletContext servletContext = request.getServletContext();
		String realPath = servletContext.getRealPath("/");
		infoList.put("realPath", realPath);

		// Agregar infoList al request
		request.setAttribute("infoList", infoList);

		RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/clientinformation.jsp");
		dispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

	private void extractClientInfo(HttpServletRequest request, HashMap<String, String> infoList) {
		infoList.put("requestURL", request.getRequestURL().toString());
		infoList.put("requestURI", request.getRequestURI());
		infoList.put("contextPath", request.getContextPath());
		infoList.put("servletPath", request.getServletPath());
		infoList.put("queryString", request.getQueryString());
		infoList.put("param1", request.getParameter("text1"));
		infoList.put("param2", request.getParameter("text2"));
		infoList.put("remoteAddr", request.getRemoteAddr());
		infoList.put("remoteHost", request.getRemoteHost());
		infoList.put("remoteUser", request.getRemoteUser());
	}

	private void extractServerInfo(HttpServletRequest request, HashMap<String, String> infoList) {
		infoList.put("serverName", request.getServerName());
		infoList.put("serverPort", String.valueOf(request.getServerPort()));
	}

	private void extractHeaderInfo(HttpServletRequest request, HashMap<String, String> infoList) {
		Enumeration<String> headers = request.getHeaderNames();
		while (headers.hasMoreElements()) {
			String header = headers.nextElement();
			infoList.put(header, request.getHeader(header));
		}
	}
}
