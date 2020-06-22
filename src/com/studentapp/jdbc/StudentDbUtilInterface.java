package com.studentapp.jdbc;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.studentapp.entity.Student;

public interface StudentDbUtilInterface {

	List<Student> getStudents() throws Exception;

	void addStudent(Student theStudent) throws Exception;

	Student getStudent(String theStudentId) throws Exception;

	void updateStudent(Student theStudent) throws Exception;

	void deleteStudent(String theStudentId) throws Exception;

	BigDecimal getNumAllRegistr() throws SQLException;

	void deleteStudents(String[] studentsId) throws SQLException;

}