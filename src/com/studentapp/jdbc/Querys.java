package com.studentapp.jdbc;

public class Querys {

    public String addStudentQuery() {
        return "INSERT INTO student (first_name, last_name, email) VALUES (?, ?, ?)";
    }

    public String getStudentsQuery() {
        return "SELECT * FROM student";
    }

    public String getStudentQuery() {
        return "SELECT * FROM student WHERE id = ?";
    }
    
    public String updateStudentQuery() {
        return "UPDATE student SET first_name = ?, last_name = ?, email = ? WHERE id = ?";
    }
    
    public String deleteStudentQuery() {
        return "DELETE FROM student WHERE id = ?";
    }
    
    public String getNumAllRegistrQuery() {
        return "SELECT COUNT(*) AS num FROM student";
    }

}

