package com.hebernate.demo.entity;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

public class FetchJoinDemo {

	public static void main(String[] args) {

		// create session factory
		SessionFactory factory = new Configuration()
								.configure("hibernate.cfg.xml")
								.addAnnotatedClass(Instructor.class)
								.addAnnotatedClass(InstructorDetail.class)
								.addAnnotatedClass(Course.class)
								.buildSessionFactory();
		
		// create session
		Session session = factory.getCurrentSession();
		
		try {			
			
			// start a transaction
			session.beginTransaction();
			
			// get the instructor from db
			int theId = 1;
			//Instructor tempInstructor = session.get(Instructor.class, theId);		
			
			// using HQL 
			
			Query<Instructor> query = session.createQuery("select i from Instructor i"
					+ " join fetch i.courses "
					+ " where i.id=:INSTRUCTOR_ID", Instructor.class);
			query.setParameter("INSTRUCTOR_ID", theId);
			
			Instructor tempInstructor = query.getSingleResult();
			
			System.out.println("luv2code: Instructor: " + tempInstructor);
		
			System.out.println("luv2code: Courses: " + tempInstructor.getCourses());
			
			// commit transaction
			session.getTransaction().commit();
			
			// close the session
			session.close();
			
			System.out.println("\nluv2code: The session is now closed!\n");
			
			// get courses for the instructor
			System.out.println("luv2code: Courses: " + tempInstructor.getCourses());
			
			System.out.println("luv2code: Done!");
		}
		finally {
			
			// add clean up code
			session.close();
			
			factory.close();
		}
	}

}





