package com.studentapp.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.studentapp.entity.Student;
import com.studentapp.utils.DBUtils;

public class StudentDbUtilImpl implements StudentDbUtilInterface {

    private static final Logger logger = Logger.getLogger(StudentDbUtilImpl.class);
    private final DataSource dataSource;
    private final Querys querys;

    public StudentDbUtilImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.querys = new Querys();
    }

    @Override
    public List<Student> getStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        try (Connection myConn = dataSource.getConnection();
             PreparedStatement myStmt = myConn.prepareStatement(querys.getStudentsQuery());
             ResultSet myRs = myStmt.executeQuery()) {
            while (myRs.next()) {
                int id = myRs.getInt("id");
                String firstName = myRs.getString("first_name");
                String lastName = myRs.getString("last_name");
                String email = myRs.getString("email");
                students.add(new Student(id, firstName, lastName, email));
            }
        }
        return students;
    }

    @Override
    public void addStudent(Student theStudent) throws SQLException {
        try (Connection myConn = dataSource.getConnection();
             PreparedStatement myStmt = myConn.prepareStatement(querys.addStudentQuery())) {
            myStmt.setString(1, theStudent.getFirstName());
            myStmt.setString(2, theStudent.getLastName());
            myStmt.setString(3, theStudent.getEmail());
            myStmt.execute();
        }
    }

    @Override
    public Student getStudent(String theStudentId) throws SQLException {
        int studentId = Integer.parseInt(theStudentId);
        try (Connection myConn = dataSource.getConnection();
             PreparedStatement myStmt = myConn.prepareStatement(querys.getStudentQuery())) {
            myStmt.setInt(1, studentId);
            try (ResultSet myRs = myStmt.executeQuery()) {
                if (myRs.next()) {
                    String firstName = myRs.getString("first_name");
                    String lastName = myRs.getString("last_name");
                    String email = myRs.getString("email");
                    return new Student(studentId, firstName, lastName, email);
                } else {
                    throw new SQLException("Could not find student id: " + studentId);
                }
            }
        }
    }

    @Override
    public void updateStudent(Student theStudent) throws SQLException {
        try (Connection myConn = dataSource.getConnection();
             PreparedStatement myStmt = myConn.prepareStatement(querys.updateStudentQuery())) {
            myStmt.setString(1, theStudent.getFirstName());
            myStmt.setString(2, theStudent.getLastName());
            myStmt.setString(3, theStudent.getEmail());
            myStmt.setInt(4, theStudent.getId());
            myStmt.execute();
        }
    }

    @Override
    public void deleteStudent(String theStudentId) throws SQLException {
        int studentId = Integer.parseInt(theStudentId);
        try (Connection myConn = dataSource.getConnection();
             PreparedStatement myStmt = myConn.prepareStatement(querys.deleteStudentQuery())) {
            myStmt.setInt(1, studentId);
            myStmt.execute();
        }
    }

    @Override
    public BigDecimal getNumAllRegistr() throws SQLException {
        try (Connection myConn = dataSource.getConnection();
             PreparedStatement myStmt = myConn.prepareStatement(querys.getNumAllRegistrQuery());
             ResultSet myRs = myStmt.executeQuery()) {
            if (myRs.next()) {
                return myRs.getBigDecimal("num");
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public void deleteStudents(String[] studentsId) throws SQLException {
        if (studentsId.length == 0) {
            return;
        }
        StringBuilder sql = new StringBuilder("DELETE FROM student WHERE id IN (");
        for (int i = 0; i < studentsId.length; i++) {
            sql.append("?");
            if (i < studentsId.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");
        try (Connection myConn = dataSource.getConnection();
             PreparedStatement myStmt = myConn.prepareStatement(sql.toString())) {
            for (int i = 0; i < studentsId.length; i++) {
                myStmt.setString(i + 1, studentsId[i]);
            }
            myStmt.execute();
        }
    }
}
