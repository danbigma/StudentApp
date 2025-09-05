package com.studentapp.controller;

import com.studentapp.jdbc.StudentDbUtilImpl;
import com.studentapp.jdbc.StudentDbUtilInterface;
import com.studentapp.web.BaseServlet;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/admin/seed")
public class SeedDemoServlet extends BaseServlet {

    @Resource(name = "jdbc/studentApp")
    private DataSource dataSource;

    private StudentDbUtilInterface utils;

    @Override
    public void init() throws ServletException {
        super.init();
        utils = new StudentDbUtilImpl(dataSource);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int count = intParam(req, "count", 100);
        try {
            utils.seedStudents(count);
        } catch (Exception e) {
            throw new ServletException("Seeding failed", e);
        }
        redirect(req, resp, "/admin");
    }
}

