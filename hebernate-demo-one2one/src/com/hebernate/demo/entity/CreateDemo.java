package com.hebernate.demo.entity;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class CreateDemo {

	public static void main(String[] args) {

		// create session factory
		SessionFactory factory = new Configuration()
								.configure("hibernate.cfg.xml")
								.addAnnotatedClass(Instructor.class)
								.addAnnotatedClass(InstructorDetail.class)
								.buildSessionFactory();
		
		// create session
		Session session = factory.getCurrentSession();
		
		try {			
			
			// create the objects
			
			Instructor tempInstructor1 = 
					new Instructor("Chad", "Darby", "darby@luv2code.com");
			
			InstructorDetail tempInstructorDetail1 =
					new InstructorDetail(
							"http://www.luv2code.com/youtube",
							"Luv 2 code!!!");		
			
			
			Instructor tempInstructor = 
					new Instructor("Madhu", "Patel", "madhu@luv2code.com");
			
			InstructorDetail tempInstructorDetail =
					new InstructorDetail(
							"http://www.youtube.com",
							"Guitar");		
			
			// associate the objects
			tempInstructor.setInstructorDetail(tempInstructorDetail);
			tempInstructor1.setInstructorDetail(tempInstructorDetail1);
			// start a transaction
			session.beginTransaction();
			

			System.out.println("Saving instructor: " + tempInstructor);
			session.save(tempInstructor);					
			session.save(tempInstructor1);
			// commit transaction
			session.getTransaction().commit();
			
			System.out.println("Done!");
		}
		finally {
			factory.close();
		}
	}

}





