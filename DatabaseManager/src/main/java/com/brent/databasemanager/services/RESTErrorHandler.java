package com.brent.databasemanager.services;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.brent.databasemanager.pojo.APIResponse;
import com.brent.databasemanager.pojo.FileParseException;
import com.brent.databasemanager.pojo.ObjectStorageException;
import com.brent.databasemanager.pojo.STTFException;
import com.brent.databasemanager.pojo.TTSCException;
import com.brent.databasemanager.pojo.TableReadException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RESTErrorHandler extends ResponseEntityExceptionHandler {
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, 
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String error = "Malformed JSON request";
		return buildResponseEntity(new APIResponse(HttpStatus.BAD_REQUEST, error, ex));
	} //end handleHttpMessageNotReadable
	
	@ExceptionHandler(NullPointerException.class)
	protected ResponseEntity<Object> handleNullPointerException(NullPointerException ex) {
		return buildResponseEntity(new APIResponse(HttpStatus.NOT_FOUND, "Null data found. Please check your "
				+ "data or contact administrator for assistance."));
	} //end handleNullPointerException
	
	@ExceptionHandler(BadSqlGrammarException.class)
	protected ResponseEntity<Object> handleBadSqlGrammarException(BadSqlGrammarException ex) {
		return buildResponseEntity(new APIResponse(HttpStatus.BAD_REQUEST, "Your request failed due to a SQL request. Please contact administrator.", ex));
	} //end handleBadSqlGrammarException
	
	@ExceptionHandler(UncategorizedSQLException.class) 
	protected ResponseEntity<Object> handleUncategorizedSQLException(UncategorizedSQLException ex) {
		return buildResponseEntity(new APIResponse(HttpStatus.EXPECTATION_FAILED, "A dependency in the SQL command failed. Please contact administrator.", ex));
	} //end handleUncategorizedSQLException
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	protected ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
		return buildResponseEntity(new APIResponse(HttpStatus.EXPECTATION_FAILED, "Record modification in the table failed.", ex));
	} //end handleDataIntegrityViolationException
	
	@ExceptionHandler(STTFException.class)
	protected ResponseEntity<Object> handleSTTFException(STTFException ex) {
		return buildResponseEntity(new APIResponse(HttpStatus.BAD_REQUEST, ex.getMessage()));
	} //end handleSTTFException
	
	@ExceptionHandler(ObjectStorageException.class)
	protected ResponseEntity<Object> handleObjectStorageException(ObjectStorageException ex) {
		return buildResponseEntity(new APIResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), ex));
	} //end handleObjectStorageException
	
	@ExceptionHandler(FileParseException.class)
	protected ResponseEntity<Object> handleFileParseException(FileParseException ex) {
		return buildResponseEntity(new APIResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
	} //end handleFileParseException
	
	@ExceptionHandler(TTSCException.class)
	protected ResponseEntity<Object> handleTTSCException(TTSCException ex) {
		return buildResponseEntity(new APIResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
	} //end handleTTSCException 
	
	@ExceptionHandler(TableReadException.class)
	protected ResponseEntity<Object> handleTableReadException(TableReadException ex) {
		return buildResponseEntity(new APIResponse(HttpStatus.BAD_REQUEST, ex.getMessage()));
	} //end handleTableReadException
	
	@ExceptionHandler(Throwable.class)
	protected ResponseEntity<Object> handleThrowable(Throwable ex) {
		return buildResponseEntity(new APIResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error " + ex.getMessage(), ex));
	} //end handleThrowable
	
	private ResponseEntity<Object> buildResponseEntity(APIResponse apiError) {
		return new ResponseEntity<Object>(apiError, apiError.getStatus());
	} //end buildResponseEntity
} //end RESTErrorHandler class
