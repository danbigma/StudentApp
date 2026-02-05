package com.studentapp.student.repository;

import com.studentapp.entity.Student;
import com.studentapp.jdbc.StudentDbUtilImpl;
import com.studentapp.jdbc.StudentDbUtilInterface;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class JdbcStudentRepository implements StudentRepository {

    private final StudentDbUtilInterface db;

    public JdbcStudentRepository(DataSource dataSource) {
        this.db = new StudentDbUtilImpl(dataSource);
    }

    @Override
    public List<Student> findAll() throws SQLException {
        try {
            return db.getStudents();
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Error listing students", e);
        }
    }

    @Override
    public List<Student> findPaged(int offset, int limit) throws SQLException {
        return db.getStudentsPaged(offset, limit);
    }

    @Override
    public Student findById(String studentId) throws SQLException {
        try {
            return db.getStudent(studentId);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Error getting student", e);
        }
    }

    @Override
    public void create(Student student) throws SQLException {
        try {
            db.addStudent(student);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Error creating student", e);
        }
    }

    @Override
    public void update(Student student) throws SQLException {
        try {
            db.updateStudent(student);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Error updating student", e);
        }
    }

    @Override
    public void deleteById(String studentId) throws SQLException {
        try {
            db.deleteStudent(studentId);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Error deleting student", e);
        }
    }

    @Override
    public void deleteMany(String[] studentIds) throws SQLException {
        db.deleteStudents(studentIds);
    }

    @Override
    public BigDecimal countAll() throws SQLException {
        return db.getNumAllRegistr();
    }

    @Override
    public void seed(int count) throws SQLException {
        db.seedStudents(count);
    }
}
