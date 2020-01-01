package com.studentapp.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.studentapp.entity.Student;
import com.studentapp.utils.DBUtils;

public class StudentDbUtil {

	private DataSource dataSource;
	
	public Querys querys = null;

	public StudentDbUtil(DataSource theDataSource) {
		dataSource = theDataSource;
	}
	
	public List<Student> getStudents() throws Exception {
		
		List<Student> students = new ArrayList<>();
		
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;
		
		try {
			
			querys = new Querys();
			
			// get a connection
			myConn = dataSource.getConnection();
			
			// create sql statement
			String sql = querys.getStudentsQuery();
			
			myStmt = myConn.createStatement();
			
			// execute query
			myRs = myStmt.executeQuery(sql);
			
			// process result set
			while (myRs.next()) {
				
				// retrieve data from result set row
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				
				// create new student object
				Student tempStudent = new Student(id, firstName, lastName, email);
				
				// add it to the list of students
				students.add(tempStudent);				
			}
			
			return students;		
		}
		finally {
			// close JDBC objects
			DBUtils.close(myConn, myStmt, myRs);
		}		
	}


	public void addStudent(Student theStudent) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
			
			querys = new Querys();
			
			// get db connection
			myConn = dataSource.getConnection();
			
			// create sql for insert
			String sql = querys.addStudentQuery();
			
			myStmt = myConn.prepareStatement(sql);
			
			// set the param values for the student
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
			
			// execute sql insert
			myStmt.execute();
		}
		finally {
			// clean up JDBC objects
			DBUtils.close(myConn, myStmt, null);
		}
	}

	public Student getStudent(String theStudentId) throws Exception {

		Student theStudent = null;
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		int studentId;
		
		try {
			
			querys = new Querys();
			
			// convert student id to int
			studentId = Integer.parseInt(theStudentId);
			
			// get connection to database
			myConn = dataSource.getConnection();
			
			// create sql to get selected student
			String sql = querys.getStudentQuery();
			
			// create prepared statement
			myStmt = myConn.prepareStatement(sql);
			
			// set params
			myStmt.setInt(1, studentId);
			
			// execute statement
			myRs = myStmt.executeQuery();
			
			// retrieve data from result set row
			if (myRs.next()) {
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				
				// use the studentId during construction
				theStudent = new Student(studentId, firstName, lastName, email);
			}
			else {
				throw new Exception("Could not find student id: " + studentId);
			}				
			
			return theStudent;
		}
		finally {
			// clean up JDBC objects
			DBUtils.close(myConn, myStmt, myRs);
		}
	}

	public void updateStudent(Student theStudent) throws Exception {
		
		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			// get db connection
			myConn = dataSource.getConnection();
			
			// create SQL update statement
			String sql = "update student set first_name = ?, last_name = ?, email = ? where id = ? ";
			
			// prepare statement
			myStmt = myConn.prepareStatement(sql);
			
			// set params
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
			myStmt.setInt(4, theStudent.getId());
			
			// execute SQL statement
			myStmt.execute();
		}
		finally {
			// clean up JDBC objects
			DBUtils.close(myConn, myStmt, null);
		}
	}

	public void deleteStudent(String theStudentId) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
			// convert student id to int
			int studentId = Integer.parseInt(theStudentId);
			
			// get connection to database
			myConn = dataSource.getConnection();
			
			// create sql to delete student
			String sql = "delete from student where id=?";
			
			// prepare statement
			myStmt = myConn.prepareStatement(sql);
			
			// set params
			myStmt.setInt(1, studentId);
			
			// execute sql statement
			myStmt.execute();
		}
		finally {
			// clean up JDBC code
			DBUtils.close(myConn, myStmt, null);
		}	
	}

	public BigDecimal getNumAllRegistr() throws SQLException {
		
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;
		
		BigDecimal num = null;
		
		try {
			
			myConn = dataSource.getConnection();
			
			String sql = "select count(*) as num from student";
			
			myStmt = myConn.createStatement();
			
			myRs = myStmt.executeQuery(sql);
			
			if (myRs.next()) {
				
				num = myRs.getBigDecimal("num");
				
			}
			
		} finally {
			// close JDBC objects
			DBUtils.close(myConn, myStmt, myRs);
		}	
		
		return num;
	}

	public void deleteStudents(String[] studentsId) throws SQLException {
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		StringBuilder sql = new StringBuilder();
		
		int i=0;
		
		try {
			
			// get connection to database
			myConn = dataSource.getConnection();
			
			sql.append(" delete ");
			sql.append(" from ");
			sql.append(" student ");
			sql.append(" where ");
			sql.append(" id in ( ");
			
			for (int j = 0; j < studentsId.length; j++) {
				sql.append(" ?, ");
			}
			
			sql.deleteCharAt(sql.lastIndexOf(","));
			sql.append(" ) ");
			
			// prepare statement
			myStmt = myConn.prepareStatement(sql.toString());
			
			// set params
			for (String id : studentsId) {
				i++;
				myStmt.setString(i, id);
			}
			
			// execute sql statement
			myStmt.execute();
		}
		finally {
			// clean up JDBC code
			DBUtils.close(myConn, myStmt, null);
		}	
		
	}
}















