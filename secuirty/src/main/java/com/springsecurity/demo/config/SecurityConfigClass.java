package com.springsecurity.demo.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfigClass extends WebSecurityConfigurerAdapter {

	// get data source bean from appConfigClass
	@Autowired
	private DataSource securityDataSource;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		// jdbc auth...
		auth.jdbcAuthentication().dataSource(securityDataSource);
		
		// use in memory auth
//		UserBuilder userBuilder = User.withDefaultPasswordEncoder();
//
//		auth.inMemoryAuthentication()
//		.withUser(userBuilder.username("john").password("test123").roles("EMPLOYEE"))
//		.withUser(userBuilder.username("mary").password("test123").roles("EMPLOYEE", "MANAGER"))
//		.withUser(userBuilder.username("susan").password("test123").roles("EMPLOYEE", "ADMIN"));
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers("/").hasRole("EMPLOYEE")
		.antMatchers("/leaders/**").hasRole("MANAGER")
		.antMatchers("/systems/**").hasRole("ADMIN")
		//.anyRequest().authenticated()
		.and()
		.formLogin()
		.loginPage("/showLoginPage")
		.loginProcessingUrl("/authenticateTheUser").permitAll()
		.and()
		.logout().permitAll()
		.and()
		.exceptionHandling().accessDeniedPage("/access-denied");
	}

}
