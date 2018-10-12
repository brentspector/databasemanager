package com.brent.databasemanager.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

abstract class APISubError {

} //end APISubError class

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
class APIValidationError extends APISubError {
	private String object;
	private  String field;
	private Object rejectedValue;
	private String message;
	
	APIValidationError(String object, String message) {
		this.object = object;
		this.message = message;
	} //end object, message constructor
} //end APIValidationError class