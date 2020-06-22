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

import com.studentapp.entity.Student;
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

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String command = request.getParameter("command");
		
		if (command==null) {
			command = "list";
		}
		
		switch (command) {
			case "delete":
				deleteStudents(request, response);
				break;
			case "list":
				listStudents(request, response);
				break;
			default:
				listStudents(request, response);
				break;
		}
		
	}
	
	private void deleteStudents(HttpServletRequest request, HttpServletResponse response) {
		
		String[] studentsId = request.getParameterValues("student");
		
		if (studentsId == null)  {
			listStudents(request, response);
		}
		
		try {
			utilsDB.deleteStudents(studentsId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		listStudents(request, response);
	}
	
	private void listStudents(HttpServletRequest request, HttpServletResponse response) {
		
		RequestDispatcher dispatcher = null;
		
        List<Student> students = null;
        
		try {
			students = utilsDB.getStudents();
			
	        request.setAttribute("studentList", students);
            
	        dispatcher = request.getRequestDispatcher("deleteStudents.jsp");
	        dispatcher.forward(request, response);
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
