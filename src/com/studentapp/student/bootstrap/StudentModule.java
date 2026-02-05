package com.studentapp.student.bootstrap;

import com.studentapp.student.repository.JdbcStudentRepository;
import com.studentapp.student.repository.StudentRepository;
import com.studentapp.student.service.StudentService;
import com.studentapp.student.service.StudentServiceImpl;

import javax.sql.DataSource;

public final class StudentModule {

    private StudentModule() {
    }

    public static StudentService buildStudentService(DataSource dataSource) {
        StudentRepository repository = new JdbcStudentRepository(dataSource);
        return new StudentServiceImpl(repository);
    }
}
