package spring.demo.aop;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import spring.demo.aop.dao.AccountDao;

public class MainAppDemo {

	public static void main(String[] args) {

		
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConfigClass.class);
		
		AccountDao aDao = context.getBean("accountDao",AccountDao.class);
		
		aDao.addAccount();
		
		context.close();
	}

}
