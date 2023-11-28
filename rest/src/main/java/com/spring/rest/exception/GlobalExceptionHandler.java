package com.spring.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler
	public ResponseEntity<CustomErrorResponse> handleException (NotFoundException exception){
		CustomErrorResponse errorResponse = new CustomErrorResponse();
		errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
		errorResponse.setMessage(exception.getMessage());	
		errorResponse.setTimeStamp(System.currentTimeMillis());		
		return new ResponseEntity<CustomErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
		
	}
	
	
	// catches all exceptions
	@ExceptionHandler
	public ResponseEntity<CustomErrorResponse> handleException (Exception exception){
		CustomErrorResponse errorResponse = new CustomErrorResponse();
		errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
		errorResponse.setMessage(exception.getMessage());	
		errorResponse.setTimeStamp(System.currentTimeMillis());		
		return new ResponseEntity<CustomErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
		
	}
}
