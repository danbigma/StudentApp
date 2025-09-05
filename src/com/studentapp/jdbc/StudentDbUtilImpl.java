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
import com.studentapp.db.Database;

public class StudentDbUtilImpl implements StudentDbUtilInterface {

    private static final Logger logger = Logger.getLogger(StudentDbUtilImpl.class);
    private final DataSource dataSource;
    private final Database db;
    private final Querys querys;

    public StudentDbUtilImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.querys = new Querys();
        this.db = new Database(dataSource);
    }

    @Override
    public List<Student> getStudents() throws SQLException {
        return db.query(conn -> {
            List<Student> students = new ArrayList<>();
            try (PreparedStatement myStmt = conn.prepareStatement(querys.getStudentsQuery());
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
        });
    }

    @Override
    public void addStudent(Student theStudent) throws SQLException {
        db.execute(conn -> {
            try (PreparedStatement myStmt = conn.prepareStatement(querys.addStudentQuery())) {
                myStmt.setString(1, theStudent.getFirstName());
                myStmt.setString(2, theStudent.getLastName());
                myStmt.setString(3, theStudent.getEmail());
                myStmt.execute();
            }
        });
    }

    @Override
    public Student getStudent(String theStudentId) throws SQLException {
        int studentId = Integer.parseInt(theStudentId);
        return db.query(conn -> {
            try (PreparedStatement myStmt = conn.prepareStatement(querys.getStudentQuery())) {
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
        });
    }

    @Override
    public void updateStudent(Student theStudent) throws SQLException {
        db.execute(conn -> {
            try (PreparedStatement myStmt = conn.prepareStatement(querys.updateStudentQuery())) {
                myStmt.setString(1, theStudent.getFirstName());
                myStmt.setString(2, theStudent.getLastName());
                myStmt.setString(3, theStudent.getEmail());
                myStmt.setInt(4, theStudent.getId());
                myStmt.execute();
            }
        });
    }

    @Override
    public void deleteStudent(String theStudentId) throws SQLException {
        int studentId = Integer.parseInt(theStudentId);
        db.execute(conn -> {
            try (PreparedStatement myStmt = conn.prepareStatement(querys.deleteStudentQuery())) {
                myStmt.setInt(1, studentId);
                myStmt.execute();
            }
        });
    }

    @Override
    public BigDecimal getNumAllRegistr() throws SQLException {
        return db.query(conn -> {
            try (PreparedStatement myStmt = conn.prepareStatement(querys.getNumAllRegistrQuery());
                 ResultSet myRs = myStmt.executeQuery()) {
                if (myRs.next()) {
                    return myRs.getBigDecimal("num");
                }
            }
            return BigDecimal.ZERO;
        });
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
        db.execute(conn -> {
            try (PreparedStatement myStmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < studentsId.length; i++) {
                    myStmt.setString(i + 1, studentsId[i]);
                }
                myStmt.execute();
            }
        });
    }

    @Override
    public void seedStudents(int count) throws SQLException {
        if (count <= 0) return;
        String sql = "INSERT INTO student (first_name, last_name, email) VALUES (?, ?, ?)";
        String[] firsts = {"Liam","Olivia","Noah","Emma","Ava","Sophia","Isabella","Mia","Lucas","Mateo","Sofia","Valentina","Camila","Daniel","Diego","Elena","Lucia","Maria","Juan","Nicolas"};
        String[] lasts = {"Garcia","Martinez","Lopez","Hernandez","Gonzalez","Rodriguez","Perez","Sanchez","Ramirez","Torres","Flores","Rivera","Gomez","Diaz","Vazquez","Romero","Suarez","Molina","Navarro","Castro"};
        String[] domains = {"gmail.com","outlook.com","yahoo.com","example.com"};
        db.tx(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < count; i++) {
                    String fn = firsts[(int)(Math.random() * firsts.length)];
                    String ln = lasts[(int)(Math.random() * lasts.length)];
                    String domain = domains[(int)(Math.random() * domains.length)];
                    String email = (fn + "." + ln + "+" + System.currentTimeMillis() + i + "@" + domain).toLowerCase();
                    ps.setString(1, fn);
                    ps.setString(2, ln);
                    ps.setString(3, email);
                    ps.addBatch();
                    if (i % 500 == 0) {
                        ps.executeBatch();
                    }
                }
                ps.executeBatch();
            }
            return null;
        });
    }

    @Override
    public List<Student> getRecentStudents(int limit) throws SQLException {
        return db.query(conn -> {
            List<Student> students = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(querys.getRecentStudentsQuery())) {
                ps.setInt(1, Math.max(1, limit));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String firstName = rs.getString("first_name");
                        String lastName = rs.getString("last_name");
                        String email = rs.getString("email");
                        students.add(new Student(id, firstName, lastName, email));
                    }
                }
            }
            return students;
        });
    }
}
