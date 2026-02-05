package com.studentapp.controller;

import com.studentapp.entity.Student;
import com.studentapp.enums.Action;
import com.studentapp.student.bootstrap.StudentModule;
import com.studentapp.student.service.StudentService;
import com.studentapp.student.service.StudentValidationException;
import com.studentapp.web.BaseServlet;
import com.studentapp.web.Web;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/admin")
public class AdminController extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(AdminController.class);

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
        logger.info("AdminController initialized successfully.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

    private void commands(HttpServletRequest request, HttpServletResponse response, Action action) throws Exception {
        switch (action) {
            case DASHBOARD:
                showDashboard(request, response);
                return;
            case ADD:
                addStudent(request, response);
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
        String studentId = request.getParameter(Web.Params.STUDENT_ID);
        boolean wantsJson = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));

        try {
            studentService.deleteStudent(studentId);
            if (wantsJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"ok\":true}");
                return;
            }
            request.setAttribute(Web.Attrs.FLASH_SUCCESS, "Student deleted");
        } catch (StudentValidationException e) {
            if (wantsJson) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"ok\":false,\"error\":\"" + e.getMessage() + "\"}");
                return;
            }
            request.setAttribute(Web.Attrs.FLASH_ERROR, e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting student", e);
            if (wantsJson) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"ok\":false,\"error\":\"Delete failed\"}");
                return;
            }
            request.setAttribute(Web.Attrs.FLASH_ERROR, "Error deleting student");
        }
        showDashboard(request, response);
    }

    private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (isGet(request)) {
            showDashboard(request, response);
            return;
        }

        int id = intParam(request, Web.Params.STUDENT_ID, -1);
        String firstName = request.getParameter(Web.Params.FIRST_NAME);
        String lastName = request.getParameter(Web.Params.LAST_NAME);
        String email = request.getParameter(Web.Params.EMAIL);

        try {
            studentService.updateStudent(id, firstName, lastName, email);
            request.setAttribute(Web.Attrs.FLASH_SUCCESS, "Student updated");
        } catch (StudentValidationException e) {
            request.setAttribute(Web.Attrs.FLASH_ERROR, e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating student", e);
            request.setAttribute(Web.Attrs.FLASH_ERROR, "Error updating student");
        }
        showDashboard(request, response);
    }

    private void addStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (isGet(request)) {
            showDashboard(request, response);
            return;
        }

        String firstName = request.getParameter(Web.Params.FIRST_NAME);
        String lastName = request.getParameter(Web.Params.LAST_NAME);
        String email = request.getParameter(Web.Params.EMAIL);

        try {
            studentService.addStudent(firstName, lastName, email);
            request.setAttribute(Web.Attrs.FLASH_SUCCESS, "Student added");
        } catch (StudentValidationException e) {
            request.setAttribute(Web.Attrs.FLASH_ERROR, e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding student", e);
            request.setAttribute(Web.Attrs.FLASH_ERROR, "Error adding student");
        }
        showDashboard(request, response);
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            BigDecimal counter = studentService.countStudents();
            request.setAttribute(Web.Attrs.NUM, counter);
        } catch (Exception e) {
            logger.error("Error counting students", e);
            request.setAttribute(Web.Attrs.NUM, BigDecimal.ZERO);
        }

        List<Student> students = studentService.getAllStudents();
        request.setAttribute(Web.Attrs.STUDENT_LIST, students);
        request.setAttribute("activeMenu", "dashboard");
        forward(request, response, Web.Views.DASHBOARD);
    }
}
