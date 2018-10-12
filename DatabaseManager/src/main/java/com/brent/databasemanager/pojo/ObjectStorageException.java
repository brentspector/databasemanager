package com.brent.databasemanager.pojo;

public class ObjectStorageException extends RuntimeException {
	public ObjectStorageException(String message) {
		super(message);
	} //end message constructor
	
	public ObjectStorageException(String message, Throwable e) {
		super(message, e);
	} //end message, throwable constructor
} //end ObjectStorageException class
