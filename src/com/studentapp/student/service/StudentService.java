package com.studentapp.student.service;

import com.studentapp.entity.Student;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface StudentService {
    List<Student> getAllStudents() throws SQLException;
    List<Student> getStudentsPaged(int offset, int limit) throws SQLException;
    Student getStudent(String studentId) throws SQLException, StudentValidationException;
    void addStudent(String firstName, String lastName, String email) throws SQLException, StudentValidationException;
    void updateStudent(int id, String firstName, String lastName, String email) throws SQLException, StudentValidationException;
    void deleteStudent(String studentId) throws SQLException, StudentValidationException;
    void deleteStudents(String[] studentIds) throws SQLException, StudentValidationException;
    BigDecimal countStudents() throws SQLException;
    void seedStudents(int count) throws SQLException, StudentValidationException;
}
