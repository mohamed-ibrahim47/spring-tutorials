package com.spring.rest.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.rest.entity.Student;
import com.spring.rest.exception.CustomErrorResponse;
import com.spring.rest.exception.NotFoundException;

@RestController
@RequestMapping("/api")
public class StudentRestController {
	
	
	private List<Student> students;
	
	
	@PostConstruct
	private void LoadData() {
		students = new ArrayList<>();
		
		students.add(new Student("Poornima", "Patel"));
		students.add(new Student("Mario", "Rossi"));
		students.add(new Student("Mary", "Smith"));
		
	}

	// define endpoint for "/students" - return list of students
	
	@GetMapping("/students")
	public List<Student> getStudents() {		
			
		return students;
	}
	
	@GetMapping("/students/{studentid}")
	public Student getStudentByID(@PathVariable int studentid ) {	
		
		if(students.size() < studentid)
			throw new NotFoundException("not found ");
			
		return students.get(studentid);
	}
	
	
}









