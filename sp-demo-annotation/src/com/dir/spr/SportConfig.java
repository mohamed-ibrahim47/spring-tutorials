package com.dir.spr;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ComponentScan("com.dir.spr")
public class SportConfig {

	//define bean for furtuneservice to inject dep
	@Bean
	public FortuneService sadFortuneService() {
		return new SadFortuneService();
	}
	
	// define bean for swim coach
	@Bean
	public Coach swimCoach () {
		return new SwimCoach(sadFortuneService());
	}
}
