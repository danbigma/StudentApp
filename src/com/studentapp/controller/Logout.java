package com.studentapp.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

@WebServlet("/logout")
public class Logout extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	static Logger log = Logger.getLogger(Logout.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			HttpSession oldSession = request.getSession(false);

			if (oldSession != null) {

				oldSession.invalidate();
				oldSession.setMaxInactiveInterval(0);

				RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.jsp");
				rd.include(request, response);
			}
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

}
