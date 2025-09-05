package com.studentapp.web;

import com.studentapp.enums.Action;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {

    protected boolean isGet(HttpServletRequest req) {
        return "GET".equalsIgnoreCase(req.getMethod());
    }

    protected boolean isPost(HttpServletRequest req) {
        return "POST".equalsIgnoreCase(req.getMethod());
    }

    protected String s(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? null : v.trim();
    }

    protected int intParam(HttpServletRequest req, String name, int def) {
        String v = s(req, name);
        if (v == null || v.isEmpty()) return def;
        try { return Integer.parseInt(v); } catch (Exception e) { return def; }
    }

    protected Action actionOf(HttpServletRequest req, Action def) {
        String a = s(req, "action");
        if (a == null || a.isEmpty()) return def;
        for (Action x : Action.values()) {
            if (x.getAction().equalsIgnoreCase(a)) return x;
        }
        return def;
    }

    protected void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher(view);
        dispatcher.forward(req, resp);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }
}

