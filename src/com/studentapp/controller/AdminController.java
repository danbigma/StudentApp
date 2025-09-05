package com.studentapp.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.studentapp.entity.Student;
import com.studentapp.enums.Action;
import com.studentapp.jdbc.StudentDbUtilImpl;
import com.studentapp.jdbc.StudentDbUtilInterface;
import com.studentapp.web.BaseServlet;
import com.studentapp.web.Web;

@WebServlet("/admin")
public class AdminController extends BaseServlet {

    private static final long serialVersionUID = 1L;

    static Logger logger = Logger.getLogger(AdminController.class);

    private StudentDbUtilInterface studentDbUtil;

    @Resource(name = "jdbc/studentApp")
    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        super.init();
        // Logger configured via classpath log4j.properties
        try {
            studentDbUtil = new StudentDbUtilImpl(dataSource);
            logger.info("Init method initial");
        } catch (Exception exc) {
            throw new ServletException(exc);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Action action = actionOf(request, Action.DASHBOARD);
            commands(request, response, action);
        } catch (Exception exc) {
            throw new ServletException(exc);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @SuppressWarnings("unused")
    private void databasecounter(HttpServletRequest request) {
        try {
            BigDecimal counter = studentDbUtil.getNumAllRegistr();
            request.setAttribute(Web.Attrs.NUM, counter);
        } catch (SQLException e) {
            logger.error("Error getting DB counter", e);
        }
    }

    private void commands(HttpServletRequest request, HttpServletResponse response, Action action) throws Exception {
        switch (action) {
            case DASHBOARD:
                showDashboard(request, response);
                return;
            case ADD:
                addStudent(request, response);
                return;
            case LOAD:
                loadStudent(request, response);
                return;
            case UPDATE:
                updateStudent(request, response);
                return;
            case DELETE:
                deleteStudent(request, response);
                return;
            case LIST:
            default:
                showDashboard(request, response);
        }
    }

    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // read student id from form data
        String theStudentId = request.getParameter(Web.Params.STUDENT_ID);
        boolean wantsJson = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));
        try {
            studentDbUtil.deleteStudent(theStudentId);
            if (wantsJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"ok\":true}");
                return;
            } else {
                request.setAttribute(Web.Attrs.FLASH_SUCCESS, "Student deleted");
            }
        } catch (Exception e) {
            if (wantsJson) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"ok\":false,\"error\":\"Delete failed\"}");
                return;
            } else {
                request.setAttribute(Web.Attrs.FLASH_ERROR, "Error deleting student");
            }
        }
        // return to dashboard
        showDashboard(request, response);
    }

    private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (isGet(request)) {
            showDashboard(request, response);
            return;
        }
        // read student info from form data
        int id = intParam(request, Web.Params.STUDENT_ID, -1);
        String firstName = request.getParameter(Web.Params.FIRST_NAME);
        String lastName = request.getParameter(Web.Params.LAST_NAME);
        String email = request.getParameter(Web.Params.EMAIL);
        // create a new student object
        Student theStudent = new Student(id, firstName, lastName, email);
        // basic validation: require all fields
        if (theStudent.getFirstName().isEmpty() || theStudent.getLastName().isEmpty() || theStudent.getEmail().isEmpty()) {
            request.setAttribute(Web.Attrs.FLASH_ERROR, "All fields are required");
            showDashboard(request, response);
            return;
        }
        // perform update on database
        try {
            studentDbUtil.updateStudent(theStudent);
            request.setAttribute(Web.Attrs.FLASH_SUCCESS, "Student updated");
        } catch (Exception e) {
            request.setAttribute(Web.Attrs.FLASH_ERROR, "Error updating student");
        }
        // return to dashboard
        showDashboard(request, response);

    }

    private void loadStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // read student id from form data
        String theStudentId = request.getParameter(Web.Params.STUDENT_ID);
        // get student from database (db util)
        Student theStudent = studentDbUtil.getStudent(theStudentId);
        // place student in the request attribute
        request.setAttribute(Web.Attrs.THE_STUDENT, theStudent);
        // send to jsp page: update-student-form.jsp
        forward(request, response, Web.Views.UPDATE_STUDENT_FORM);
    }

    private void addStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (isGet(request)) {
            showDashboard(request, response);
            return;
        }
        // read student info from form data
        String firstName = request.getParameter(Web.Params.FIRST_NAME);
        String lastName = request.getParameter(Web.Params.LAST_NAME);
        String email = request.getParameter(Web.Params.EMAIL);
        // create a new student object
        Student theStudent = new Student(firstName, lastName, email);
        // basic validation: require all fields
        if (!theStudent.getFirstName().isEmpty() && !theStudent.getLastName().isEmpty() && !theStudent.getEmail().isEmpty()) {
            try {
                studentDbUtil.addStudent(theStudent);
                request.setAttribute(Web.Attrs.FLASH_SUCCESS, "Student added");
            } catch (Exception e) {
                request.setAttribute(Web.Attrs.FLASH_ERROR, "Error adding student");
            }
        } else {
            request.setAttribute(Web.Attrs.FLASH_ERROR, "All fields are required");
        }
        // return to dashboard
        showDashboard(request, response);
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response) throws Exception {
        databasecounter(request);
        List<Student> students = studentDbUtil.getStudents();
        request.setAttribute(Web.Attrs.STUDENT_LIST, students);
        forward(request, response, Web.Views.DASHBOARD);
    }

}
