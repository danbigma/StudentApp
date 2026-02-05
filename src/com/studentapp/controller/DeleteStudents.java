package com.studentapp.controller;

import com.studentapp.entity.Student;
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
import java.util.List;

@WebServlet("/admin/deletestudents")
public class DeleteStudents extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(DeleteStudents.class);

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
        logger.info("DeleteStudents servlet initialized successfully.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        listStudents(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] studentsId = request.getParameterValues(Web.Params.STUDENT_CHECKBOX);
        try {
            studentService.deleteStudents(studentsId);
            request.getSession().setAttribute(Web.Attrs.FLASH_SUCCESS, "Students deleted successfully.");
        } catch (StudentValidationException e) {
            request.getSession().setAttribute(Web.Attrs.FLASH_ERROR, e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting students", e);
            request.getSession().setAttribute(Web.Attrs.FLASH_ERROR, "Could not delete students due to a server error.");
        }
        redirect(request, response, "/admin/deletestudents");
    }

    private void listStudents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Student> students = studentService.getAllStudents();
            request.setAttribute(Web.Attrs.STUDENT_LIST, students);
            request.setAttribute("activeMenu", "delete");
            forward(request, response, Web.Views.DELETE_STUDENTS);
        } catch (Exception e) {
            logger.error("Error listing students for deletion", e);
            throw new ServletException("Could not display the student list.", e);
        }
    }
}
