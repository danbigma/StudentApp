package com.studentapp.controller;

import com.studentapp.student.bootstrap.StudentModule;
import com.studentapp.student.service.StudentService;
import com.studentapp.student.service.StudentValidationException;
import com.studentapp.web.BaseServlet;
import com.studentapp.web.Web;

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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int count = intParam(req, "count", 100);
        try {
            studentService.seedStudents(count);
            req.getSession().setAttribute(Web.Attrs.FLASH_SUCCESS, "Demo students created.");
        } catch (StudentValidationException e) {
            req.getSession().setAttribute(Web.Attrs.FLASH_ERROR, e.getMessage());
        } catch (Exception e) {
            throw new ServletException("Seeding failed", e);
        }
        redirect(req, resp, "/admin");
    }
}
