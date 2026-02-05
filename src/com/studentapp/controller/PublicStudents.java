package com.studentapp.controller;

import com.google.gson.Gson;
import com.studentapp.entity.Student;
import com.studentapp.student.bootstrap.StudentModule;
import com.studentapp.student.service.StudentService;
import com.studentapp.web.BaseServlet;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "PublicStudents", urlPatterns = { "/students" })
public class PublicStudents extends BaseServlet {

    @Resource(name = "jdbc/studentApp")
    private DataSource dataSource;

    private StudentService studentService;

    @Override
    public void init() throws ServletException {
        super.init();
        if (dataSource == null) {
            throw new ServletException("DataSource 'jdbc/studentApp' is not available. Check server configuration (context.xml) and database connectivity.");
        }
        this.studentService = StudentModule.buildStudentService(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int limit = intParam(req, "limit", 12);
        int offset = intParam(req, "offset", 0);
        boolean wantsJson = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))
                || "json".equalsIgnoreCase(req.getParameter("format"));

        try {
            List<Student> page = studentService.getStudentsPaged(offset, limit);
            if (wantsJson) {
                resp.setContentType("application/json;charset=UTF-8");
                Map<String, Object> data = new HashMap<>();
                data.put("items", page);
                data.put("nextOffset", offset + page.size());
                new Gson().toJson(data, resp.getWriter());
                return;
            }

            req.setAttribute("students", page);
            req.setAttribute("nextOffset", offset + page.size());
            forward(req, resp, "/public/students.jsp");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
