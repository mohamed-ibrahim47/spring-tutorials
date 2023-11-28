package com.dir.spr;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanScopeDemoApp {

	public static void main(String[] args) {

		ClassPathXmlApplicationContext context =
				new ClassPathXmlApplicationContext("beanScope-applicationContext.xml");
				
		Coach theCoach = context.getBean("myCoach", Coach.class);

		Coach coach = context.getBean("myCoach", Coach.class);
		
		boolean result = (theCoach == coach);
		
		System.out.println("\nPointing to the same object: " + result);
		
		System.out.println("\nMemory location for theCoach: " + theCoach.hashCode());

		System.out.println("\nMemory location for alphaCoach: " + coach.hashCode() + "\n");
	
		context.close();
	}

}








