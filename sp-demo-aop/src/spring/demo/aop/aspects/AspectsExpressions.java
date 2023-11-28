package spring.demo.aop.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AspectsExpressions {
	
	
	//@Before("execution(public void spring.demo.aop.dao.AccountDao.addAccount())") for specific class
	//@Before("execution(public void add*())" ) for any method starts wz add
	//@Before("execution(public void addAccount(*))") with 1 parameter of any type
	//@Before("execution(public void addAccount(..))") with any number of params of any type
	//@Before("execution(public void addAccount(spring.demo.aop.dao.AccountDao))") with 1 parameter of accountdao type
	//@Before("execution(public void spring.demo.aop.dao.*.*(..))") any method in any class in this package
	
	
	//combine pointcuts
		// create pointcut for getter methods
		@Pointcut("execution(* spring.demo.aop.dao.*.get*(..))")
		private void getter() {}
		
		// create pointcut for setter methods
		@Pointcut("execution(* spring.demo.aop.dao.*.set*(..))")
		private void setter() {}
		
		// create pointcut: include package ... exclude getter/setter
		@Pointcut("forDaoPackage() && !(getter() || setter())")
		private void forDaoPackageNoGetterSetter() {}
		
		
		@Pointcut("execution(public void spring.demo.aop.dao.*.*(..)))")
		public void gerericPointCut() {}
		

}
