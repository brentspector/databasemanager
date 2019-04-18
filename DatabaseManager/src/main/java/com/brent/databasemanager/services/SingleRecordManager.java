package com.brent.databasemanager.services;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.brent.databasemanager.pojo.APIResponse;
import com.brent.databasemanager.pojo.RecordManagerException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SingleRecordManager {
	
	@Autowired
	ConnectionManager conn;
	
	@Autowired
	ObjectMapper objmap;
	
	public String addRecordToTable(String url, String user, String pass, String tableName, String recordData) {
		
		try {
			//Verify connection
			conn.getConnection(url, user, pass);
			
			//Convert data to map
			Map<String, String> record = objmap.readValue(recordData, new TypeReference<Map<String, String>>(){});
						
			//Populate values into sql string
			ArrayList<Object> sqlValues = new ArrayList<Object>();
			String sql = "Insert Into " + tableName;
			StringBuilder sqlColumns = new StringBuilder(" (");
			StringBuilder sqlParams = new StringBuilder (" VALUES (");			
			convertToSQLType(record, tableName, sqlValues, sqlColumns, sqlParams);
			
			//Append and execute
			sqlColumns.replace(sqlColumns.length()-1, sqlColumns.length(), ")");
			sqlParams.replace(sqlParams.length()-1, sqlParams.length(), ")");
						
			try {
				conn.executePreparedStatement(
						sql + sqlColumns + sqlParams, sqlValues.toArray());
			} catch (DataIntegrityViolationException e) {
				throw new RecordManagerException("There was an error inserting the following: " + 
						StringUtils.collectionToCommaDelimitedString(sqlValues) + 
						" caused by " + e.getMostSpecificCause(), e);
			} //end try-catch block
			
			return objmap.writeValueAsString(new APIResponse(HttpStatus.OK, "Record was added successfully."));
		} catch (SQLRecoverableException e) {
			throw new RecordManagerException("Connection to target db failed. Please verify connection details.");
		} catch (SQLException e) {
			throw new RecordManagerException("Error getting database details.", e);
		} catch (JsonParseException e) {
			throw new RecordManagerException("Error parsing JSON data.", e);
		} catch (JsonMappingException e) {
			throw new RecordManagerException("Error mapping JSON data.", e);
		} catch (IOException e) {
			throw new RecordManagerException("Error on input data.", e);
		} finally {
			//Return connection
			try {
				conn.endConnection();
			} catch (SQLException e) {
				throw new RecordManagerException("Error closing connection.", e);
			} //end try-catch block
		} //end try-catch-finally block
	} //end addRecordToTable
	
	public String editRecordInTable(String url, String user, String pass, String tableName, String primaryKeyList, String recordData) {
		try {
			//Verify connection
			conn.getConnection(url, user, pass);
			
			//Convert data to map
			Map<String, String> record = objmap.readValue(recordData, new TypeReference<Map<String, String>>(){});
			Map<String, String> keyList = objmap.readValue(primaryKeyList, new TypeReference<Map<String, String>>(){});
						
			//Populate values into sql string
			String sql = "Update " + tableName + " SET ";
			ArrayList<Object> sqlValues = new ArrayList<Object>();
			StringBuilder sqlColumns = new StringBuilder("");
			StringBuilder sqlParams = new StringBuilder ("");			
			convertToSQLType(record, tableName, sqlValues, sqlColumns, sqlParams);
			
			//Restructure 
			sqlColumns.replace(sqlColumns.length()-1, sqlColumns.length(), "");
			StringBuilder updateStatement = new StringBuilder("");
			for(String s:sqlColumns.toString().split(",")) {
				updateStatement.append(s + " = ?,");
			} //end for each
			updateStatement.replace(updateStatement.length()-1, updateStatement.length(), "")
			.append(" WHERE ");
			
			//Convert primary key for WHERE condition
			sqlColumns.setLength(0);
			convertToSQLType(keyList, tableName, sqlValues, sqlColumns, sqlParams);
			StringBuilder whereStatement = new StringBuilder("");
			for(String s:sqlColumns.toString().split(",")) {
				whereStatement.append(s + " = ? AND ");
			} //end for
			whereStatement.replace(whereStatement.length()-5, whereStatement.length(), "");		
			
			//Check for non-singular records
			List<Map<String, Object>> tableData = conn.queryMultipleRows("select * from " + tableName +
					" WHERE " + whereStatement, new Object[] {sqlValues.get(sqlValues.size()-1)});
			if(tableData.size() < 1) {
				throw new RecordManagerException("No records found for " + keyList.keySet() + " with value of " + sqlValues.get(sqlValues.size()-1));
			} //end if 
			else if (tableData.size() > 1) {
				throw new RecordManagerException("Too many records found for " + keyList.keySet() + " with value of " + sqlValues.get(sqlValues.size()-1));
			} //end else if
			
			try {
				conn.executePreparedStatement(
						sql + updateStatement + whereStatement, sqlValues.toArray());
			} catch (DataIntegrityViolationException e) {
				throw new RecordManagerException("There was an error modifying the record with the following: " + 
						StringUtils.collectionToCommaDelimitedString(sqlValues) +
						" caused by " + e.getLocalizedMessage(), e);
			} //end try-catch block
			
			return objmap.writeValueAsString(new APIResponse(HttpStatus.OK, "Record was updated successfully."));
		} catch (SQLRecoverableException e) {
			throw new RecordManagerException("Connection to target db failed. Please verify connection details.");
		} catch (SQLException e) {
			throw new RecordManagerException("Error getting database details.", e);
		} catch (JsonParseException e) {
			throw new RecordManagerException("Error parsing JSON data.", e);
		} catch (JsonMappingException e) {
			throw new RecordManagerException("Error mapping JSON data.", e);
		} catch (IOException e) {
			throw new RecordManagerException("Error on input data.", e);
		} finally {
			//Return connection
			try {
				conn.endConnection();
			} catch (SQLException e) {
				throw new RecordManagerException("Error closing connection.", e);
			} //end try-catch block
		} //end try-catch-finally block
	} //end editRecordInTable
	
	public String deleteRecordFromTable(String url, String user, String pass, String tableName, String recordData) {
		try {
			//Verify connection
			conn.getConnection(url, user, pass);
			
			//Convert data to map
			Map<String, String> record = objmap.readValue(recordData, new TypeReference<Map<String, String>>(){});
			
			//Build SQL string
			String sql = "DELETE FROM " + tableName + " WHERE ";
			ArrayList<Object> sqlValues = new ArrayList<Object>();
			StringBuilder sqlColumns = new StringBuilder("");
			StringBuilder sqlParams = new StringBuilder ("");			
			convertToSQLType(record, tableName, sqlValues, sqlColumns, sqlParams);
			
			//Generate WHERE statement
			sqlColumns.replace(sqlColumns.length()-1, sqlColumns.length(), "");
			StringBuilder whereStatement = new StringBuilder("");
			for(String s:sqlColumns.toString().split(",")) {
				whereStatement.append(s + " = ? AND ");
			} //end for
			whereStatement.replace(whereStatement.length()-5, whereStatement.length(), "");
			
			//Check for non-singular records
			List<Map<String, Object>> tableData = conn.queryMultipleRows("select * from " + tableName +
					" WHERE " + whereStatement, sqlValues.toArray());
			if(tableData.size() < 1) {
				throw new RecordManagerException("No records found for " + sqlColumns.toString() + " with value of " + Arrays.toString(sqlValues.toArray()));
			} //end if 
			else if (tableData.size() > 1) {
				throw new RecordManagerException("Too many records found for " + sqlColumns.toString() + " with value of " + Arrays.toString(sqlValues.toArray()));
			} //end else if
			
			try {
				conn.executePreparedStatement(
						sql +
						whereStatement, sqlValues.toArray());
			} catch (DataIntegrityViolationException e) {
				throw new RecordManagerException("There was an error modifying the record with the following: " + 
						StringUtils.collectionToCommaDelimitedString(sqlValues) +
						" caused by " + e.getMostSpecificCause(), e);
			} //end try-catch block
			
			return objmap.writeValueAsString(new APIResponse(HttpStatus.OK, "Record was deleted successfully."));
		} catch (SQLRecoverableException e) {
			throw new RecordManagerException("Connection to target db failed. Please verify connection details.");
		} catch (SQLException e) {
			throw new RecordManagerException("Error getting database details.", e);
		} catch (JsonParseException e) {
			throw new RecordManagerException("Error parsing JSON data.", e);
		} catch (JsonMappingException e) {
			throw new RecordManagerException("Error mapping JSON data.", e);
		} catch (IOException e) {
			throw new RecordManagerException("Error on input data.", e);
		} finally {
			//Return connection
			try {
				conn.endConnection();
			} catch (SQLException e) {
				throw new RecordManagerException("Error closing connection.", e);
			} //end try-catch block
		} //end try-catch-finally block
	} //end deleteRecordFromTable
	
	private void convertToSQLType(Map<String, String> record, String tableName, ArrayList<Object> sqlValues, 
			StringBuilder sqlColumns, StringBuilder sqlParams) throws SQLException {
		Map<String, Integer> tableColumns = conn.getTableColumnTypes(tableName);
		record.forEach((k,v) -> {
			switch(tableColumns.get(k)) {
			case Types.BIGINT:
				try {
					sqlValues.add(Long.valueOf(v));
				} catch (NullPointerException e) {
					throw new RecordManagerException(k + " value was null when parsing to LONG.");
				} catch (NumberFormatException e) {
					throw new RecordManagerException(k + " could not parse " + v + " to LONG.", e);
				} //end try-catch Long
				break;
			case Types.BOOLEAN:
				sqlValues.add(Boolean.valueOf(v));
				break;
			case Types.DATE:
				try {
					DateTimeFormatter form = new DateTimeFormatterBuilder().appendPattern(
							"M/d/yyyy").toFormatter();
					LocalDate ld = LocalDate.parse((CharSequence) v,form);
					sqlValues.add(Date.valueOf(ld));
				} catch (Exception e) {
					e.printStackTrace();
					throw new RecordManagerException("Your date in " + k + " did not format properly with M/d/yyyy and " + 
							" produced an error.", e);
				} //end try-catch block Date
				break;
			case Types.DOUBLE:
				try {
					sqlValues.add(Double.valueOf(v));
				} catch (NullPointerException e) {
					throw new RecordManagerException(k + " value was null when parsing to DOUBLE.");
				} catch (NumberFormatException e) {
					throw new RecordManagerException(k + " could not parse " + v + " to DOUBLE.", e);
				} //end try-catch Double
				break;
			case Types.FLOAT:
				try {
					sqlValues.add(Float.valueOf(v));
				} catch (NullPointerException e) {
					throw new RecordManagerException(k + " value was null when parsing to FLOAT.");
				} catch (NumberFormatException e) {
					throw new RecordManagerException(k + " could not parse " + v + " to FLOAT.", e);
				} //end try-catch Float
				break;
			case Types.INTEGER:
				try {
					sqlValues.add(Integer.valueOf(v));
				} catch (NullPointerException e) {
					throw new RecordManagerException(k + " value was null when parsing to INTEGER.");
				} catch (NumberFormatException e) {
					throw new RecordManagerException(k + " could not parse " + v + " to INTEGER.", e);
				} //end try-catch Integer
				break;
			case Types.NUMERIC:
				try {
					sqlValues.add(Double.valueOf(v));
				} catch (NullPointerException e) {
					throw new RecordManagerException(k + " value was null when parsing to NUMERIC.");
				} catch (NumberFormatException e) {
					throw new RecordManagerException(k + " could not parse " + v + " to NUMERIC.", e);
				} //end try-catch Numeric
				break;
			case Types.TIMESTAMP:
				DateTimeFormatter form = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
				try {
					LocalDateTime ldt = LocalDateTime.parse((CharSequence) v, form);
					sqlValues.add(Timestamp.valueOf(ldt));
				} catch (Exception e) {
					e.printStackTrace();
					throw new RecordManagerException("Your timestamp in " + k + " did not format properly to" +
							" ISO_LOCAL_DATE_TIME and produced an error.", e);
				} //end try-catch block Timestamp
				break;
			case Types.VARCHAR:
				sqlValues.add(String.valueOf(v));
				break;
			default: 
				sqlValues.add(v);
			} //end switch
			
			sqlColumns.append(k + ",");
			sqlParams.append("?,");
		});
	} //end convertToSQLType
} //end SingleRecordManager
