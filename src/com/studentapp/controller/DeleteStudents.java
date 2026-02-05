package com.studentapp.controller;

import java.io.IOException;
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

@WebServlet("/admin/deletestudents")
public class DeleteStudents extends BaseServlet {

    private static final long serialVersionUID = 1L;
    
    private static final Logger logger = Logger.getLogger(DeleteStudents.class);

    private StudentDbUtilInterface utilsDB;

    @Resource(name = "jdbc/studentApp")
    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        super.init();
        if (dataSource == null) {
            throw new ServletException("DataSource 'jdbc/studentApp' is not available. Check server configuration (context.xml) and database connectivity.");
        }
        this.utilsDB = new StudentDbUtilImpl(dataSource);
        logger.info("DeleteStudents servlet initialized successfully.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // GET request always displays the list of students.
        listStudents(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // POST request handles the deletion.
        String[] studentsId = request.getParameterValues(Web.Params.STUDENT_CHECKBOX);
        if (studentsId == null || studentsId.length == 0) {
            // Use session for flash messages due to Post-Redirect-Get pattern.
            request.getSession().setAttribute(Web.Attrs.FLASH_ERROR, "No students selected for deletion.");
            redirect(request, response, "/admin/deletestudents");
            return;
        }
        try {
            utilsDB.deleteStudents(studentsId);
            request.getSession().setAttribute(Web.Attrs.FLASH_SUCCESS, "Students deleted successfully.");
        } catch (Exception e) {
            logger.error("Error deleting students", e);
            request.getSession().setAttribute(Web.Attrs.FLASH_ERROR, "Could not delete students due to a server error.");
        }
        // Redirect back to the list page to show the result and prevent form re-submission.
        redirect(request, response, "/admin/deletestudents");
    }

    private void listStudents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Student> students = utilsDB.getStudents();
            request.setAttribute(Web.Attrs.STUDENT_LIST, students);
            request.setAttribute("activeMenu", "delete");
            forward(request, response, Web.Views.DELETE_STUDENTS);
        } catch (Exception e) {
            logger.error("Error listing students for deletion", e);
            throw new ServletException("Could not display the student list.", e);
        }
    }
}
