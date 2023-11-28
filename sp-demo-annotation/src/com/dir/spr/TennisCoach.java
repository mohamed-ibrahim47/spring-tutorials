package com.dir.spr;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("tennis")
//@Component > default bean id("tennisCoach")
@Scope("prototype")
public class TennisCoach implements Coach {

	@Autowired
	@Qualifier("happyFortuneService")
	private FortuneService FortuneService;
	
	
	public TennisCoach() {
		
	}
	
	@Override
	public String getDailyWorkout() {
		return "tennis";
	}

	@Override
	public String getDailyFortune() {
		return FortuneService.getFortune();
	}
	
	@PostConstruct
	public void init() {
		System.out.println("init");
	}
	
	@PreDestroy
	public void dest() {
		System.out.println("destroy");
	}
//	@Autowired
//	public void setFortuneService(FortuneService fortuneService) {
//		FortuneService = fortuneService;
//	}

}








