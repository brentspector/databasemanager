package com.brent.databasemanager.pojo;

public class RecordManagerException extends RuntimeException {

	public RecordManagerException(String message) {
		super(message);
	} //end message constructor
	
	public RecordManagerException(String message, Throwable e) {
		super(message, e);
	} //end message, exception constructor
} //end RecordManagerException
