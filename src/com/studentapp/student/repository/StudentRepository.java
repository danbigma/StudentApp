package com.studentapp.student.repository;

import com.studentapp.entity.Student;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface StudentRepository {
    List<Student> findAll() throws SQLException;
    List<Student> findPaged(int offset, int limit) throws SQLException;
    Student findById(String studentId) throws SQLException;
    void create(Student student) throws SQLException;
    void update(Student student) throws SQLException;
    void deleteById(String studentId) throws SQLException;
    void deleteMany(String[] studentIds) throws SQLException;
    BigDecimal countAll() throws SQLException;
    void seed(int count) throws SQLException;
}
