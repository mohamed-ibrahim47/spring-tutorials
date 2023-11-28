package com.spring.testDB;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TestDBServlet
 */
@WebServlet("/TestDBServlet")
public class TestDBServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String user = "user";
		String pass = "user";
		String jdbcurl = "jdbc:mysql://localhost:3306/web_customer_tracker?useSSl=false&serverTimezone=UTC";
		String jdbcDriver = "com.mysql.jdbc.Driver";
		try {
			PrintWriter out = response.getWriter();
			Class.forName(jdbcDriver);
			Connection myConnection = DriverManager.getConnection(jdbcurl, user, pass);
			out.println("done");
			myConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

}
