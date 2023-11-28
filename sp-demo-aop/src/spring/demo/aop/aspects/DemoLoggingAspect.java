package spring.demo.aop.aspects;

import java.util.List;
import java.util.logging.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import spring.demo.aop.Account;

@Aspect
@Component
@Order(1)
public class DemoLoggingAspect {
	
 	@Before("spring.demo.aop.aspects.AspectsExpressions.gerericPointCut()")
	public void beforeAddAcctAspectAdvice(JoinPoint joinPoint) {
		
 		//get method name and params
 		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		System.out.println(methodSignature);
		//get paramter value
 		Object[] objects= joinPoint.getArgs();
 		for (Object object : objects) {
 			System.out.println(object);

		}
 		
		System.out.println("\n =========>>>> before add acct");
	}
 	
 	@AfterReturning( pointcut="execution(public void findAccounts())", returning="res")
	public void afterReturnFindAccountsAdvice(JoinPoint joinPoint,List<Account> res) {
		
 		//get method name and params
 		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		System.out.println(methodSignature);
		//get paramter value
 		Object[] objects= joinPoint.getArgs();
 		for (Object object : objects) {
 			System.out.println(object);

		}
 		
		// post-process the data ...  modify it :-)
		
		// convert the account names to uppercase
		for (Account tempAccount : res) {
			
			// get uppercase version of name
			String theUpperName = tempAccount.getName().toUpperCase();
			
			// update the name on the account
			tempAccount.setName(theUpperName);
		} 		
		System.out.println("\n =========>>>> afterReturning find accts"+res);
	}
 	
 	
	@AfterThrowing(
			pointcut="execution(public void findAccounts(..))",
			throwing="theExc")
	public void afterThrowingFindAccountsAdvice(
					JoinPoint theJoinPoint, Throwable theExc) {
		
		// print out which method we are advising on
		String method = theJoinPoint.getSignature().toShortString();
		System.out.println("\n=====>>> Executing @AfterThrowing on method: " + method);
		
		// log the exception
		System.out.println("\n=====>>> The exception is: " + theExc);
	
	}
	
	// after run before afterthrowing like  finally in try catch
 	@After("spring.demo.aop.aspects.AspectsExpressions.gerericPointCut()")
	public void afterAddAcctAspectAdvice(JoinPoint joinPoint) {
		
 		//get method name and params
 		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		System.out.println(methodSignature);
		//get paramter value
 		Object[] objects= joinPoint.getArgs();
 		for (Object object : objects) {
 			System.out.println(object);

		}
 		
		System.out.println("\n =========>>>> after add acct");
	}
 	
 	
 	// use logger(same output stream like Spring) to resolve print order issue (instead of using println()  ) to keep them in sync
	private Logger myLogger = Logger.getLogger(getClass().getName());
	// around executes before and after 
	@Around("execution(public void findAccounts(..))")	
	public Object aroundFindAccounts(
			ProceedingJoinPoint theProceedingJoinPoint) throws Throwable {
		
		// print out method we are advising on
		String method = theProceedingJoinPoint.getSignature().toShortString();
		myLogger.info("\n=====>>> Executing @Around on method: " + method);
		
		// get begin timestamp
		long begin = System.currentTimeMillis();
		
		// now, let's execute the method
		Object result = null;
		
		try {
			// this execute the method
			result = theProceedingJoinPoint.proceed();
		} catch (Exception e) {
			// log the exception
			myLogger.warning(e.getMessage());

			// rethrow exception or use result="default ,,,,"
			throw e;
		}
		
		// get end timestamp
		long end = System.currentTimeMillis();
		
		// compute duration and display it
		long duration = end - begin;
		myLogger.info("\n=====> Duration: " + duration / 1000.0 + " seconds");
		
		return result;
	}
}
