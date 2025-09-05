package com.studentapp.controller;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/health/db")
public class DbHealthServlet extends HttpServlet {
    @Resource(name = "jdbc/studentApp")
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");

        long start = System.nanoTime();
        boolean up = false;
        String error = null;
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1");
             ResultSet rs = ps.executeQuery()) {
            up = rs.next();
        } catch (SQLException e) {
            up = false;
            error = e.getMessage();
        }
        long durationMs = (System.nanoTime() - start) / 1_000_000L;

        // Pool stats via reflection to avoid compile-time dependency on Tomcat JDBC classes
        Integer active = invokeInt(dataSource, "getActive");
        Integer idle = invokeInt(dataSource, "getIdle");
        Integer maxActive = invokeInt(dataSource, "getMaxActive");
        Integer maxWait = invokeInt(dataSource, "getMaxWait");

        StringBuilder json = new StringBuilder(256);
        json.append('{')
            .append("\"status\":\"").append(up ? "UP" : "DOWN").append("\",")
            .append("\"dbTimeMs\":").append(durationMs).append(',')
            .append("\"pool\":{");
        if (active != null) json.append("\"active\":").append(active).append(',');
        if (idle != null) json.append("\"idle\":").append(idle).append(',');
        if (maxActive != null) json.append("\"maxActive\":").append(maxActive).append(',');
        if (maxWait != null) json.append("\"maxWait\":").append(maxWait).append(',');
        // remove trailing comma if present
        if (json.charAt(json.length()-1) == ',') json.setLength(json.length()-1);
        json.append('}');
        if (!up && error != null) {
            json.append(',').append("\"error\":\"").append(escape(error)).append('"');
        }
        json.append('}');

        if (!up) {
            resp.setStatus(503);
        }
        resp.getWriter().write(json.toString());
    }

    private Integer invokeInt(Object target, String method) {
        try {
            return (Integer) target.getClass().getMethod(method).invoke(target);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
