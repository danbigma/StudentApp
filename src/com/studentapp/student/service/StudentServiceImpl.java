package com.studentapp.student.service;

import com.studentapp.entity.Student;
import com.studentapp.student.repository.StudentRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class StudentServiceImpl implements StudentService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final StudentRepository repository;

    public StudentServiceImpl(StudentRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Student> getAllStudents() throws SQLException {
        return repository.findAll();
    }

    @Override
    public List<Student> getStudentsPaged(int offset, int limit) throws SQLException {
        int safeOffset = Math.max(0, offset);
        int safeLimit = Math.max(1, limit);
        return repository.findPaged(safeOffset, safeLimit);
    }

    @Override
    public Student getStudent(String studentId) throws SQLException, StudentValidationException {
        validateStudentId(studentId);
        return repository.findById(studentId);
    }

    @Override
    public void addStudent(String firstName, String lastName, String email) throws SQLException, StudentValidationException {
        Student student = buildStudent(0, firstName, lastName, email, false);
        repository.create(student);
    }

    @Override
    public void updateStudent(int id, String firstName, String lastName, String email) throws SQLException, StudentValidationException {
        if (id <= 0) {
            throw new StudentValidationException("Invalid student id.");
        }
        Student student = buildStudent(id, firstName, lastName, email, true);
        repository.update(student);
    }

    @Override
    public void deleteStudent(String studentId) throws SQLException, StudentValidationException {
        validateStudentId(studentId);
        repository.deleteById(studentId);
    }

    @Override
    public void deleteStudents(String[] studentIds) throws SQLException, StudentValidationException {
        if (studentIds == null || studentIds.length == 0) {
            throw new StudentValidationException("No students selected.");
        }
        for (String studentId : studentIds) {
            validateStudentId(studentId);
        }
        repository.deleteMany(studentIds);
    }

    @Override
    public BigDecimal countStudents() throws SQLException {
        return repository.countAll();
    }

    @Override
    public void seedStudents(int count) throws SQLException, StudentValidationException {
        if (count <= 0) {
            throw new StudentValidationException("Seed count must be greater than zero.");
        }
        repository.seed(count);
    }

    private Student buildStudent(int id, String firstName, String lastName, String email, boolean includeId) throws StudentValidationException {
        String safeFirstName = normalizeRequired(firstName, "First name is required.");
        String safeLastName = normalizeRequired(lastName, "Last name is required.");
        String safeEmail = normalizeRequired(email, "Email is required.");
        if (!EMAIL_PATTERN.matcher(safeEmail).matches()) {
            throw new StudentValidationException("Email format is invalid.");
        }
        return includeId ? new Student(id, safeFirstName, safeLastName, safeEmail)
                : new Student(safeFirstName, safeLastName, safeEmail);
    }

    private String normalizeRequired(String value, String message) throws StudentValidationException {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty()) {
            throw new StudentValidationException(message);
        }
        return normalized;
    }

    private void validateStudentId(String studentId) throws StudentValidationException {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new StudentValidationException("Student id is required.");
        }
        try {
            int parsed = Integer.parseInt(studentId.trim());
            if (parsed <= 0) {
                throw new StudentValidationException("Student id is invalid.");
            }
        } catch (NumberFormatException e) {
            throw new StudentValidationException("Student id is invalid.");
        }
    }
}
