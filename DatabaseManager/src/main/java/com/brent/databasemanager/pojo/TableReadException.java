package com.brent.databasemanager.pojo;

public class TableReadException extends RuntimeException {
	public TableReadException(String message) {
		super(message);
	} //end message constructor
	
	public TableReadException(String message, Throwable e) {
		super(message, e);
	} //end message, throwable constructor
} //end TableReadException
