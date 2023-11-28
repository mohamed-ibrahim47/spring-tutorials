package spring.demo.aop.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(2)
public class PerformApiAnalytics {
	
 	@Before("spring.demo.aop.aspects.AspectsExpressions.gerericPointCut()")
	public void performApiAnalytics() {
		
		System.out.println("\n =========>>>> performApiAnalytics");
	}

}
