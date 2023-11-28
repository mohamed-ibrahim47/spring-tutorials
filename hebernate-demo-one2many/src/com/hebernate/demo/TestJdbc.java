package com.hebernate.demo;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestJdbc {

	public static void main(String[] args) {
		String jdbcurl = "jdbc:mysql://localhost:3306/hb-03-one-to-many?useSSl=false&serverTimezone=UTC";
		String user ="user";
		String pass = "user";
		
		try {
			Connection connection = DriverManager.getConnection(jdbcurl, user, pass);
			System.out.println("ss");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
