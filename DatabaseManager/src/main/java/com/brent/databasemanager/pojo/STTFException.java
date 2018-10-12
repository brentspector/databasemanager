package com.brent.databasemanager.pojo;

public class STTFException extends RuntimeException {
	public STTFException(String message) {
		super(message);
	} //end message constructor

	public STTFException(String message, Throwable e) {
		super(message, e);
	} //end message, throwable constructor
} //end STTFException class
