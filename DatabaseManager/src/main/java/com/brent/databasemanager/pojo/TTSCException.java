package com.brent.databasemanager.pojo;

public class TTSCException extends RuntimeException {
	public TTSCException(String message) {
		super(message);
	} //end message constructor
	
	public TTSCException(String message, Throwable e) {
		super(message, e);
	} //end message, exception constructor
} //end TTSCException class
