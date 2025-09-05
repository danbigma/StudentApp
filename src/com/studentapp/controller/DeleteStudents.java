package com.studentapp.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.studentapp.entity.Student;
import com.studentapp.enums.Action;
import com.studentapp.jdbc.StudentDbUtilImpl;
import com.studentapp.jdbc.StudentDbUtilInterface;
import com.studentapp.web.BaseServlet;
import com.studentapp.web.Web;

@WebServlet("/admin/deletestudents")
public class DeleteStudents extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private StudentDbUtilInterface utilsDB;

    @Resource(name = "jdbc/studentApp")
    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        super.init();

        try {
            utilsDB = new StudentDbUtilImpl(dataSource);
        } catch (Exception exc) {
            throw new ServletException(exc);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Action action = actionOf(request, Action.LIST);
        switch (action) {
            case DELETE:
                deleteStudents(request, response);
                break;
            case LIST:
            default:
                listStudents(request, response);
        }
    }

    private void deleteStudents(HttpServletRequest request, HttpServletResponse response) {
        String[] studentsId = request.getParameterValues(Web.Params.STUDENT_CHECKBOX);
        if (studentsId == null) {
            listStudents(request, response);
            return;
        }
        try {
            utilsDB.deleteStudents(studentsId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        listStudents(request, response);
    }

    private void listStudents(HttpServletRequest request, HttpServletResponse response) {
        List<Student> students = null;
        try {
            students = utilsDB.getStudents();
            request.setAttribute(Web.Attrs.STUDENT_LIST, students);
            forward(request, response, Web.Views.DELETE_STUDENTS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
