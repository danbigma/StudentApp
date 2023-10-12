package com.studentapp.controller;

import java.io.IOException;

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession oldSession = request.getSession(false);

            if (oldSession != null) {
                // Invalida la sesión
                oldSession.invalidate();
                log.info("Sesión cerrada por el usuario.");
            }

            // Redirige al usuario a la página principal u otra página apropiada después del cierre de sesión
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        } catch (Exception e) {
            // Maneja cualquier excepción
            log.error("Error al cerrar la sesión: " + e.getMessage(), e);
            // Redirige a una página de error o manejo de errores
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}

