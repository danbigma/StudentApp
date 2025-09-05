package com.studentapp.controller;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@WebServlet("/health")
public class HealthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final long START_TIME_MS = System.currentTimeMillis();

    @Resource(name = "jdbc/studentApp")
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");

        // App info
        String version = getImplementationVersion();
        long now = System.currentTimeMillis();
        long uptimeMs = now - START_TIME_MS;
        String nowIso = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault()).format(Instant.ofEpochMilli(now));

        // DB check
        boolean dbUp = false;
        long dbMs = -1L;
        try {
            long t0 = System.nanoTime();
            dbUp = pingDb();
            dbMs = (System.nanoTime() - t0) / 1_000_000L;
        } catch (Exception ignored) {
            dbUp = false;
        }

        boolean up = dbUp; // overall status ties to DB here
        if (!up) {
            resp.setStatus(503);
        }

        String json = new StringBuilder(256)
                .append('{')
                .append("\"status\":\"").append(up ? "UP" : "DOWN").append("\",")
                .append("\"app\":{")
                .append("\"version\":\"").append(escape(version)).append("\",")
                .append("\"uptimeMs\":").append(uptimeMs).append(',')
                .append("\"now\":\"").append(nowIso).append("\"}")
                .append(',')
                .append("\"db\":{")
                .append("\"status\":\"").append(dbUp ? "UP" : "DOWN").append("\",")
                .append("\"timeMs\":").append(dbMs)
                .append("}}")
                .toString();

        resp.getWriter().write(json);
    }

    private boolean pingDb() throws SQLException {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1");
             ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }

    private String getImplementationVersion() {
        String fromPackage = HealthServlet.class.getPackage() != null
                ? HealthServlet.class.getPackage().getImplementationVersion()
                : null;
        if (fromPackage != null) {
            return fromPackage;
        }
        // Try MANIFEST
        try {
            URL url = getServletContext().getResource("/META-INF/MANIFEST.MF");
            if (url != null) {
                try (InputStream is = url.openStream()) {
                    Manifest mf = new Manifest(is);
                    Attributes attrs = mf.getMainAttributes();
                    String v = attrs.getValue("Implementation-Version");
                    if (v != null) return v;
                }
            }
        } catch (Exception ignored) {}
        return "unknown";
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
