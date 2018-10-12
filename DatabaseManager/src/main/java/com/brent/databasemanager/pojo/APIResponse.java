package com.brent.databasemanager.pojo;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class APIResponse {
	
	private HttpStatus status;
	private String userMessage;
	private String debugMessage;
	private List<APISubError> subErrors;
	
	public APIResponse(HttpStatus status, String message) {
		this.status = status;
		this.userMessage = message;
	} //end status, userMessage constructor
	
	public APIResponse(HttpStatus status, String error, Throwable ex) {
		this.status = status;
		this.userMessage = error;
		this.debugMessage = ex.getLocalizedMessage();
	} //end status, userMessage, debugMessage constructor
} //end APIError class
