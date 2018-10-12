package com.brent.databasemanager.pojo;

public class FileParseException extends RuntimeException {

	public FileParseException(String message) {
		super(message);
	} //end message constructor
	
	public FileParseException(String message, Throwable e) {
		super(message ,e);
	} //end message, throwable constructor
} //end FileParseException class
