package com.servlets.jdbc;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;


@WebServlet("/studentController")
public class MainServletController extends HttpServlet {
	
    private static final long serialVersionUID = 1L;

    private StudentDbUtil studentDbUtil;
    
    @Resource(name = "jdbc/studentApp")
    private DataSource dataSource;
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        // create our student db util ... and pass in the conn pool / datasource
        try {
            studentDbUtil = new StudentDbUtil(dataSource);
        }
        catch (Exception exc) {
            throw new ServletException(exc);
        }
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
        	
        	databasecounter(request, response);
        	
            String theCommand = request.getParameter("command");
            
            commands(request, response, theCommand);
                
        }
        catch (Exception exc) {
            throw new ServletException(exc);
        }
        
    }

	private void databasecounter(HttpServletRequest request, HttpServletResponse response) {
		
		RequestDispatcher dispatcher = null;
		
		try {
			
			BigDecimal counter = studentDbUtil.getNumAllRegistr();
			
			request.setAttribute("num", counter);
			
			dispatcher = request.getRequestDispatcher("databasecounter.jsp");
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	private void commands(HttpServletRequest request, HttpServletResponse response, String theCommand)
			throws Exception {
		
		if (theCommand == null) {
		    theCommand = "list";
		}
		
		// route to the appropriate method
		switch (theCommand) {
		
		    case "list":
		        listStudents(request, response);
		        break;
		        
		    case "add":
		        addStudent(request, response);
		        break;
		        
		    case "load":
		        loadStudent(request, response);
		        break;
		        
		    case "update":
		        updateStudent(request, response);
		        break;
		    
		    case "delete":
		        deleteStudent(request, response);
		        break;
		        
		    default:
		        listStudents(request, response);
		}
	}

    private void deleteStudent(HttpServletRequest request, HttpServletResponse response)
        throws Exception {

        // read student id from form data
        String theStudentId = request.getParameter("studentId");
        
        // delete student from database
        studentDbUtil.deleteStudent(theStudentId);
        
        // send them back to "list students" page
        listStudents(request, response);
    }

    private void updateStudent(HttpServletRequest request, HttpServletResponse response)
        throws Exception {

        // read student info from form data
        int id = Integer.parseInt(request.getParameter("studentId"));
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        
        // create a new student object
        Student theStudent = new Student(id, firstName, lastName, email);
        
        // perform update on database
        studentDbUtil.updateStudent(theStudent);
        
        // send them back to the "list students" page
        listStudents(request, response);
        
    }

    private void loadStudent(HttpServletRequest request, HttpServletResponse response) 
        throws Exception {

        // read student id from form data
        String theStudentId = request.getParameter("studentId");
        
        // get student from database (db util)
        Student theStudent = studentDbUtil.getStudent(theStudentId);
        
        // place student in the request attribute
        request.setAttribute("THE_STUDENT", theStudent);
        
        // send to jsp page: update-student-form.jsp
        RequestDispatcher dispatcher = 
                request.getRequestDispatcher("/update-student-form.jsp");
        dispatcher.forward(request, response);      
    }

    private void addStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // read student info from form data
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");       
        
        // create a new student object
        Student theStudent = new Student(firstName, lastName, email);
        
        // add the student to the database
        studentDbUtil.addStudent(theStudent);
                
        // send back to main page (the student list)
        listStudents(request, response);
    }

    private void listStudents(HttpServletRequest request, HttpServletResponse response) 
        throws Exception {

        // get students from db util
        List<Student> students = studentDbUtil.getStudents();
        
        // add students to the request
        request.setAttribute("STUDENT_LIST", students);
                
        // send to JSP page (view)
        RequestDispatcher dispatcher = request.getRequestDispatcher("/list-students.jsp");
        dispatcher.forward(request, response);
    }

}













