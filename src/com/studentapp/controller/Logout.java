package com.studentapp.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import com.studentapp.web.BaseServlet;

@WebServlet("/logout")
public class Logout extends BaseServlet {

    private static final long serialVersionUID = 1L;
    static Logger log = Logger.getLogger(Logout.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Forzar uso de POST para logout por seguridad (CSRF)
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
                log.info("Sesión cerrada por el usuario.");
            }
            redirect(request, response, "/login.jsp");
        } catch (Exception e) {
            log.error("Error al cerrar la sesión: " + e.getMessage(), e);
            redirect(request, response, "/error.jsp");
        }
    }
}
