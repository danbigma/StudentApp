package com.studentapp.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

@WebServlet("/login")
public class Login extends HttpServlet {

	private static final long serialVersionUID = 1L;

	static Logger log = Logger.getLogger(Login.class);
	
	private final String username = "admin";
	private final String password = "admin";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// get request parameters for username and password
		String usrname = request.getParameter("login");
		String pasword = request.getParameter("password");
		boolean savesession = request.getParameter("savesession") != null;

		try {
			if (this.username.equals(usrname) && this.password.equals(pasword)) {
				// get the old session and invalidate
				HttpSession oldSession = request.getSession(false);
				if (oldSession != null) {
					oldSession.invalidate();
				}
				// generate a new session
				HttpSession newSession = request.getSession(true);

				if (savesession) {
					// setting session to expiry in 30 mins
					newSession.setMaxInactiveInterval(30 * 60);
				} else {
					// setting session to expiry in 1 mins
					newSession.setMaxInactiveInterval(1 * 60);
				}
				newSession.setAttribute("username", username);

				Cookie message = new Cookie("message", "Welcome");
				response.addCookie(message);
				response.sendRedirect(request.getContextPath() + "/admin");
			} else {
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.jsp");
				rd.include(request, response);
			}
		} catch (Exception e) {
			log.error(e.getStackTrace());
		}

	}

}
