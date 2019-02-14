package com.studentapp.jdbc;

public class Querys {
	
	public String addStudentQuery() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(" insert into ");
		sb.append(" student ");
		sb.append(" (first_name, last_name, email) ");
		sb.append(" values ");
		sb.append(" (?, ?, ?) ");
		
		return sb.toString();
		
	}
	
	public String getStudentsQuery() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(" select ");
		sb.append(" * ");
		sb.append(" from ");
		sb.append(" student ");
		sb.append(" order by last_name ");
		
		return sb.toString();
		
	}
	
	public String getStudentQuery() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(" select ");
		sb.append(" * ");
		sb.append(" from ");
		sb.append(" student ");
		sb.append(" where ");
		sb.append(" id = ? ");
		
		return sb.toString();
		
	}

}