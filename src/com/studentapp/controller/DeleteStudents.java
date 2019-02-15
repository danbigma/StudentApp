package com.studentapp.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.studentapp.jdbc.Student;
import com.studentapp.jdbc.StudentDbUtil;

@WebServlet("/deletestudents")
public class DeleteStudents extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private StudentDbUtil utilsDB;
	
    @Resource(name = "jdbc/studentApp")
	private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        super.init();
			
		try {
			utilsDB = new StudentDbUtil(dataSource);
		} catch (Exception exc) {
            throw new ServletException(exc);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		RequestDispatcher dispatcher = null;
		
        List<Student> students = null;
        
		try {
			students = utilsDB.getStudents();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        request.setAttribute("studentList", students);
                
        dispatcher = request.getRequestDispatcher("deleteVariousStudents.jsp");
        dispatcher.forward(request, response);
		
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
