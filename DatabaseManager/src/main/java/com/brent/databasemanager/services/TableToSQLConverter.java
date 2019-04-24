package com.brent.databasemanager.services;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.brent.databasemanager.DatabaseManagerApplication;
import com.brent.databasemanager.pojo.APIResponse;
import com.brent.databasemanager.pojo.RecordManagerException;
import com.brent.databasemanager.pojo.TTSCException;
import com.brent.databasemanager.pojo.TableContents;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

@Service
public class TableToSQLConverter {
	
	private static final Logger log = LoggerFactory.getLogger(DatabaseManagerApplication.class);
	
	@Autowired
	ConnectionManager conn;
	
	@Autowired
	S3Manager s3;
	
	@Autowired
	ObjectMapper objmap;
	
	private Pattern pattern;
	private Matcher matcher;
	
	@Transactional
	public String constructTable(String url, String user, String pass, String fileins, String tableName, boolean rowIndexing, String tColumns)
	{
		try
		{
			//Check and initiate connection to database
			conn.getConnection(url, user, pass);
			
			//Initialize objects
			TableContents tc = s3.GetBucketContents(fileins);
			Map<String, String> columnTypes = new HashMap<String, String>();
			Map<String, String> dateFormats = new HashMap<String, String>();
			Map<String, String> timestampFormats = new HashMap<String, String>();
			
			//Convert string to map
			ObjectReader reader = new ObjectMapper().readerFor(Map.class);
			Map<String, String> tableColumns;
			try {
				tableColumns = reader.readValue(tColumns);
			} catch (IOException e) {
				throw new TTSCException("Unable to convert table columns to map.", e);
			} //end try-catch block

			//Create table
			createTable(tableName, dateFormats, timestampFormats, columnTypes, tc.getHeaders(), rowIndexing, tableColumns);		
			
			//Insert table data
			ArrayList<Map<String, String>> contents = tc.getContents();
			String sql = "INSERT INTO " + tableName;	
			insertContents(contents, tableName, columnTypes, dateFormats, 
					timestampFormats, sql, rowIndexing);			
			return objmap.writeValueAsString(new APIResponse(HttpStatus.OK, "Database table created successfully."));
		} catch (JsonProcessingException e) {
			throw new TTSCException("Unable to convert response to JSON.", e);
		} catch (SQLRecoverableException e) {
			throw new TTSCException("Connection to target db failed.", e);
		} catch (SQLDataException e) {
			throw new TTSCException("Malformed SQL caused from invalid data.", e);
		} catch (SQLException e) {
			throw new TTSCException("An error occurred while working with database. Please verify credentials.", e);
		} finally {
			//Return connection
			try {
				conn.endConnection();
			} catch (SQLException e) {
				throw new TTSCException("Error while closing the connection.", e);
			} //end try-catch block
		} //end try-catch-finally block
	} //end constructTable
	
	public String addRecords(String url, String user, String pass, String tableName, TableContents tc) {
		try {			
			//Check and initiate connection to database
			conn.getConnection(url, user, pass);
		
			//Initialize objects
			Map<String, String> columnTypes = new HashMap<String, String>();
			Map<String, String> dateFormats = new HashMap<String, String>();
			Map<String, String> timestampFormats = new HashMap<String, String>();
			ArrayList<Map<String, String>> contents = tc.getContents();
			String sql = "INSERT INTO " + tableName;	
			
			//Validate table
			if(validateTable(tableName, conn.getMetaData())) {
				throw new TTSCException("Table does not exist in database. Unable to add.");
			} //end if
			
			//Insert table data
			populateFormatsAndTypes(tableName, contents.get(0), dateFormats, timestampFormats, columnTypes);
			contents = (ArrayList<Map<String, String>>) contents.subList(1, contents.size()-1);
			insertContents(contents, tableName, columnTypes, dateFormats, timestampFormats, sql, false);
			return objmap.writeValueAsString(new APIResponse(HttpStatus.OK, "Database table records added successfully."));
		} catch (SQLRecoverableException e) {
			throw new TTSCException("Connection to target db failed.", e);
		} catch (SQLDataException e) {
			throw new TTSCException("Malformed SQL caused from invalid data.", e);
		} catch (SQLException e) {
			throw new TTSCException("An error occurred while working with database. Please verify credentials.", e);
		} catch (JsonProcessingException e) {
			throw new TTSCException("Unable to convert response to JSON.", e);
		} finally {
			//Return connection
			try {
				conn.endConnection();
			} catch (SQLException e) {
				throw new TTSCException("Error while closing the connection.", e);
			} //end try-catch block
		} //end try-catch-finally block
	} //end addRecords
	
	public String editRecords(String url, String user, String pass, String tableName, TableContents tc, String primaryKeyList) {
		try {			
			//Check and initiate connection to database
			conn.getConnection(url, user, pass);
		
			//Initialize objects
			Map<String, String> columnTypes = new HashMap<String, String>();
			Map<String, String> dateFormats = new HashMap<String, String>();
			Map<String, String> timestampFormats = new HashMap<String, String>();
			ArrayList<String> keyList = objmap.readValue(primaryKeyList, new TypeReference<ArrayList<String>>(){});
			ArrayList<Map<String, String>> contents = tc.getContents();
			String sql = "UPDATE " + tableName + " SET ";	
			
			//Validate table
			if(validateTable(tableName, conn.getMetaData())) {
				throw new TTSCException("Table does not exist in database. Unable to add.");
			} //end if
			
			//Modify table data
			populateFormatsAndTypes(tableName, contents.get(0), dateFormats, timestampFormats, columnTypes);
			contents = (ArrayList<Map<String, String>>) contents.subList(1, contents.size()-1);
			updateRecords(contents, tableName, columnTypes, dateFormats, timestampFormats, keyList, sql);
			return objmap.writeValueAsString(new APIResponse(HttpStatus.OK, "Database table records added successfully."));
		} catch (SQLRecoverableException e) {
			throw new TTSCException("Connection to target db failed.", e);
		} catch (SQLDataException e) {
			throw new TTSCException("Malformed SQL caused from invalid data.", e);
		} catch (SQLException e) {
			throw new TTSCException("An error occurred while working with database. Please verify credentials.", e);
		} catch (JsonProcessingException e) {
			throw new TTSCException("Unable to convert response to JSON.", e);
		} catch (IOException e) {
			throw new TTSCException("Error on input data.", e);
		} finally {
			//Return connection
			try {
				conn.endConnection();
			} catch (SQLException e) {
				throw new TTSCException("Error while closing the connection.", e);
			} //end try-catch block
		} //end try-catch-finally block
	} //end editRecords

	private boolean validateTable(String tableName, DatabaseMetaData meta) throws SQLException
	{
		ResultSet res;
		pattern = Pattern.compile("^[a-zA-Z0-9_~`!@#\\$%\\^&\\*\\.\\,\\?]*$");
		matcher = pattern.matcher(tableName);
		if(!matcher.lookingAt())
		{
			throw new TTSCException("Your table name contains one of the following characters and "
					+ "cannot be accepted - : ; , ' \"");
		} //end if

		res = meta.getTables(null, null, tableName.toUpperCase(), new String[] {"TABLE"});
		if(res.next())
		{
			return false;
		} //end if
		res.close();
		return true;
	} //end validateTable	
	
	private void populateFormatsAndTypes(String tableName, Map<String, String> formatList, 
			Map<String, String> dateFormats, Map<String, String> timestampFormats, Map<String, String> columnTypes) {
		try {
			Map<String, Integer> tableColumns = conn.getTableColumnTypes(tableName);
			formatList.forEach((k,v)->{
				switch(tableColumns.get(k)) {
				case Types.DATE:
					dateFormats.put(k, (v == null ? "MM-DD-YYYY" : v));
					break;
				case Types.TIMESTAMP:
					timestampFormats.put(k, v == null ? "MM-DD-YYYY HH24:MI:SS" : v);
					break;
				} //end switch
			});
			tableColumns.forEach((k,v)->{
				columnTypes.put(k, String.valueOf(v));
			});
		} catch (SQLException e) {
			throw new TTSCException("Unable to convert first row due to an error.", e);
		} //end try-catch
	} ///end populateFormatsAndTypes
	
	private void createTable(String tableName, Map<String, String> dateFormats, 
			Map<String, String> timestampFormats, Map<String, String> columnTypes, 
			ArrayList<String> columnNames, boolean rowIndexing, Map tableColumnMapping) throws SQLException
	{
		String sql;
		
		//Validate table name
		if(!validateTable(tableName, conn.getMetaData())) {
			throw new TTSCException("Table " + tableName + " already exists in target database. "
					+ "Creation cancelled.");
		}
		
		sql = "CREATE TABLE " + tableName + "(";
		if(rowIndexing)
		{
			columnTypes.put("IdRow", "Number");
			sql += "IdRow Number Not Null Primary Key,";
		} //end if
		for(String columnName : columnNames)
		{
			//{ "ID": "Date", "IDvar1": "MM-DD-YYYY", "IDvar2": 0, "Name": "Number", "Namevar1": 1, "Namevar2": 0 }
			String columnType = (String) tableColumnMapping.get(columnName);
			if(columnType == null) {
				columnType = "Text";
			} //end if
			matcher = pattern.matcher(columnName);
			if(!matcher.lookingAt())
			{
				throw new TTSCException(columnName + " contains one of the following characters and "
						+ "cannot be accepted - : ; , ' \"");
			} //end if
			matcher = pattern.matcher(columnType);
			if(!matcher.lookingAt())
			{
				throw new TTSCException(columnType + " contains one of the following characters and "
						+ "cannot be accepted - : ; , ' \"");
			} //end if
			else
			{
				switch(columnType)
				{
				case "Text":
					sql += columnName + " VARCHAR2(255),";
					break;
				case "Number":
					String precision = (String) tableColumnMapping.get(columnName + "var1");
					String scale = (String) tableColumnMapping.get(columnName + "var2");
					sql += columnName + " " + columnType + "(" + 
							(precision == null ? "10" : precision) + "," +
							(scale == null ? "0" : scale) + "),";
					break;
				case "Date":
					String dateForm = (String) tableColumnMapping.get(columnName + "var1");
					dateFormats.put(columnName, (dateForm == null ? "MM-DD-YYYY" : dateForm));
					sql += columnName + " " + columnType + ",";
					break;
				case "DTime":
					String timeForm = (String) tableColumnMapping.get(columnName + "var1");
					timestampFormats.put(columnName, timeForm == null ? "MM-DD-YYYY HH24:MI:SS" : timeForm);
					sql += columnName + " TIMESTAMP,";
					break;
				default:
					sql += columnName + " " + columnType + ",";
				} //end switch
			} //end else
			columnTypes.put(columnName, columnType);
		} //end for
		
		sql = sql.substring(0, sql.length()-1) + ")";
		conn.executeString(sql);
	} //end createTable
	
	@SuppressWarnings("unchecked")
	private void insertContents(ArrayList<Map<String, String>> contents, String tableName, 
			Map<String, String> columnTypes, Map<String, String> dateFormats, 
			Map<String, String> timestampFormats, String sql, boolean row) throws SQLException
	{
		ArrayList<Object> sqlValues = new ArrayList<Object>();
		StringBuilder sqlColumns = new StringBuilder(" (");
		StringBuilder sqlParams = new StringBuilder(" VALUES (");
		int iterationCount = 1;
		for(Map m: contents)
		{
			if(row) {
				sqlValues.add(iterationCount);
				sqlColumns.append("IdRow,");
				sqlParams.append("?,");
			} //end if
			m.forEach((k,v) -> {		
				//key is Nombre and value is 1
				switch(columnTypes.get(k)) 
				{
				case "Number":
					sqlValues.add(Double.parseDouble((String) v));
					sqlColumns.append(k + ",");
					sqlParams.append("?,");
					break;
				case "91":
					//Date fall-through
				case "Date":
					try {
						DateTimeFormatter form = new DateTimeFormatterBuilder().appendPattern(
								dateFormats.get(k)).toFormatter();
						LocalDate ld = LocalDate.parse((CharSequence) v,form);
						sqlValues.add(Date.valueOf(ld));
						sqlColumns.append(k + ",");
						sqlParams.append("?,");
					} catch (Exception e) {
						e.printStackTrace();
						throw new TTSCException("Your date format " + dateFormats.get(k) + 
								" produced an error.", e);
					} //end try-catch block Date
					break;
				case "93":
					//Timestamp fall-through
				case "DTime":
					DateTimeFormatter form;
					String pattern = timestampFormats.get(k);
					try {
						switch(pattern) {
						case "YYYY-MM-DDTHH24:MI:SS":
							form = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
							break;
						default:
							form = new DateTimeFormatterBuilder().appendPattern(
								timestampFormats.get(k)).toFormatter();
						} //end switch
						LocalDateTime ldt = LocalDateTime.parse((CharSequence) v, form);
						sqlValues.add(Timestamp.valueOf(ldt));
						sqlColumns.append(k + ",");
						sqlParams.append("?,");
					} catch (Exception e) {
						e.printStackTrace();
						throw new TTSCException("Your timestamp format " + timestampFormats.get(k) +
								" produced an error.", e);
					} //end try-catch block DTime
					break;
				case "-5":
					//Bigint
					try {
						sqlValues.add(Long.valueOf((String)v));
						sqlColumns.append(k + ",");
						sqlParams.append("?,");
					} catch (NullPointerException e) {
						throw new RecordManagerException(k + " value was null when parsing to LONG.");
					} catch (NumberFormatException e) {
						throw new RecordManagerException(k + " could not parse " + v + " to LONG.", e);
					} //end try-catch Long
					break;
				case "16":
					//Boolean
					sqlValues.add(Boolean.valueOf((String)v));
					sqlColumns.append(k + ",");
					sqlParams.append("?,");
					break;
				case "8":
					//Double
					try {
						sqlValues.add(Double.valueOf((String)v));
						sqlColumns.append(k + ",");
						sqlParams.append("?,");
					} catch (NullPointerException e) {
						throw new RecordManagerException(k + " value was null when parsing to DOUBLE.");
					} catch (NumberFormatException e) {
						throw new RecordManagerException(k + " could not parse " + v + " to DOUBLE.", e);
					} //end try-catch Double
					break;
				case "6":
					//Float
					try {
						sqlValues.add(Float.valueOf((String)v));
						sqlColumns.append(k + ",");
						sqlParams.append("?,");
					} catch (NullPointerException e) {
						throw new RecordManagerException(k + " value was null when parsing to FLOAT.");
					} catch (NumberFormatException e) {
						throw new RecordManagerException(k + " could not parse " + v + " to FLOAT.", e);
					} //end try-catch Float
					break;
				case "4":
					//Integer
					try {
						sqlValues.add(Integer.valueOf((String)v));
						sqlColumns.append(k + ",");
						sqlParams.append("?,");
					} catch (NullPointerException e) {
						throw new RecordManagerException(k + " value was null when parsing to INTEGER.");
					} catch (NumberFormatException e) {
						throw new RecordManagerException(k + " could not parse " + v + " to INTEGER.", e);
					} //end try-catch Integer
					break;
				case "2":
					//Numeric
					try {
						sqlValues.add(Double.valueOf((String)v));
						sqlColumns.append(k + ",");
						sqlParams.append("?,");
					} catch (NullPointerException e) {
						throw new RecordManagerException(k + " value was null when parsing to NUMERIC.");
					} catch (NumberFormatException e) {
						throw new RecordManagerException(k + " could not parse " + v + " to NUMERIC.", e);
					} //end try-catch Numeric
					break;
				case "12":
					//Varchar and Varchar2
					sqlValues.add(String.valueOf(v));
					sqlColumns.append(k + ",");
					sqlParams.append("?,");
					break;
				default:
					sqlValues.add(v);			
					sqlColumns.append(k + ",");
					sqlParams.append("?,");
				} //end switch
			}); //end forEach
			sqlColumns.replace(sqlColumns.length()-1, sqlColumns.length(), ")");
			sqlParams.replace(sqlParams.length()-1, sqlParams.length(), ")");
						
			try {
				conn.executePreparedStatement(
						sql + sqlColumns + sqlParams, sqlValues.toArray());
				sqlValues.clear();
				sqlColumns.delete(0, sqlColumns.length());
				sqlColumns.append(" (");
				sqlParams.delete(0, sqlParams.length());
				sqlParams.append(" VALUES (");
			} catch (DataIntegrityViolationException e) {
				throw new TTSCException("There was an error inserting the following: " + 
						StringUtils.collectionToCommaDelimitedString(sqlValues), e);
			} //end try-catch block
			iterationCount++;
		} //end for
	} //end insertContents
	
	@SuppressWarnings("unchecked")
	private void updateRecords(ArrayList<Map<String, String>> contents, String tableName, 
			Map<String, String> columnTypes, Map<String, String> dateFormats, 
			Map<String, String> timestampFormats, ArrayList<String> primaryKeys, String sql) throws SQLException {
	
		//Initialize objects
		ArrayList<Object> sqlValues = new ArrayList<Object>();
		ArrayList<Object> whereValues = new ArrayList<Object>();
		StringBuilder updateStatement = new StringBuilder("");
		StringBuilder whereStatement = new StringBuilder("");
		
		//Loop through contents and form each update
		for(Map m: contents)
		{
			m.forEach((k,v) -> {		
				//key is Nombre and value is 1
				switch(columnTypes.get(k)) 
				{
				case "91":
					//Date
					try {
						DateTimeFormatter form = new DateTimeFormatterBuilder().appendPattern(
								dateFormats.get(k)).toFormatter();
						LocalDate ld = LocalDate.parse((CharSequence) v,form);
						sqlValues.add(Date.valueOf(ld));
						updateStatement.append(k + " = ?,");
						if(primaryKeys.contains(k) ) {
							whereStatement.append(k + " = ? AND ");
							whereValues.add(Date.valueOf(ld));
						} //end if
					} catch (Exception e) {
						e.printStackTrace();
						throw new TTSCException("Your date format " + dateFormats.get(k) + 
								" produced an error.", e);
					} //end try-catch block Date
					break;
				case "93":
					//Timestamp
					DateTimeFormatter form;
					String pattern = timestampFormats.get(k);
					try {
						switch(pattern) {
						case "YYYY-MM-DDTHH24:MI:SS":
							form = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
							break;
						default:
							form = new DateTimeFormatterBuilder().appendPattern(
								timestampFormats.get(k)).toFormatter();
						} //end switch
						LocalDateTime ldt = LocalDateTime.parse((CharSequence) v, form);
						sqlValues.add(Timestamp.valueOf(ldt));
						updateStatement.append(k + " = ?,");
						if(primaryKeys.contains(k) ) {
							whereStatement.append(k + " = ? AND ");
							whereValues.add(Timestamp.valueOf(ldt));
						} //end if
					} catch (Exception e) {
						e.printStackTrace();
						throw new TTSCException("Your timestamp format " + timestampFormats.get(k) +
								" produced an error.", e);
					} //end try-catch block DTime
					break;
				case "-5":
					//Bigint
					try {
						sqlValues.add(Long.valueOf((String)v));
						updateStatement.append(k + " = ?,");
						if(primaryKeys.contains(k) ) {
							whereStatement.append(k + " = ? AND ");
							whereValues.add(Long.valueOf((String)v));
						} //end if
					} catch (NullPointerException e) {
						throw new RecordManagerException(k + " value was null when parsing to LONG.");
					} catch (NumberFormatException e) {
						throw new RecordManagerException(k + " could not parse " + v + " to LONG.", e);
					} //end try-catch Long
					break;
				case "16":
					//Boolean
					sqlValues.add(Boolean.valueOf((String)v));
					updateStatement.append(k + " = ?,");
					if(primaryKeys.contains(k) ) {
						whereStatement.append(k + " = ? AND ");
						whereValues.add(Boolean.valueOf((String)v));
					} //end if
					break;
				case "8":
					//Double
					try {
						sqlValues.add(Double.valueOf((String)v));
						updateStatement.append(k + " = ?,");
						if(primaryKeys.contains(k) ) {
							whereStatement.append(k + " = ? AND ");
							whereValues.add(Double.valueOf((String)v));
						} //end if
					} catch (NullPointerException e) {
						throw new RecordManagerException(k + " value was null when parsing to DOUBLE.");
					} catch (NumberFormatException e) {
						throw new RecordManagerException(k + " could not parse " + v + " to DOUBLE.", e);
					} //end try-catch Double
					break;
				case "6":
					//Float
					try {
						sqlValues.add(Float.valueOf((String)v));
						updateStatement.append(k + " = ?,");
						if(primaryKeys.contains(k) ) {
							whereStatement.append(k + " = ? AND ");
							whereValues.add(Float.valueOf((String)v));
						} //end if
					} catch (NullPointerException e) {
						throw new RecordManagerException(k + " value was null when parsing to FLOAT.");
					} catch (NumberFormatException e) {
						throw new RecordManagerException(k + " could not parse " + v + " to FLOAT.", e);
					} //end try-catch Float
					break;
				case "4":
					//Integer
					try {
						sqlValues.add(Integer.valueOf((String)v));
						updateStatement.append(k + " = ?,");
						if(primaryKeys.contains(k) ) {
							whereStatement.append(k + " = ? AND ");
							whereValues.add(Integer.valueOf((String)v));
						} //end if
					} catch (NullPointerException e) {
						throw new RecordManagerException(k + " value was null when parsing to INTEGER.");
					} catch (NumberFormatException e) {
						throw new RecordManagerException(k + " could not parse " + v + " to INTEGER.", e);
					} //end try-catch Integer
					break;
				case "2":
					//Numeric
					try {
						sqlValues.add(Double.valueOf((String)v));
						updateStatement.append(k + " = ?,");
						if(primaryKeys.contains(k) ) {
							whereStatement.append(k + " = ? AND ");
							whereValues.add(Double.valueOf((String)v));
						} //end if
					} catch (NullPointerException e) {
						throw new RecordManagerException(k + " value was null when parsing to NUMERIC.");
					} catch (NumberFormatException e) {
						throw new RecordManagerException(k + " could not parse " + v + " to NUMERIC.", e);
					} //end try-catch Numeric
					break;
				case "12":
					//Varchar and Varchar2
					sqlValues.add(String.valueOf(v));
					updateStatement.append(k + " = ?,");
					if(primaryKeys.contains(k) ) {
						whereStatement.append(k + " = ? AND ");
						whereValues.add(String.valueOf(v));
					} //end if
					break;
				default:
					sqlValues.add(v);			
					updateStatement.append(k + " = ?,");
					if(primaryKeys.contains(k) ) {
						whereStatement.append(k + " = ? AND ");
						whereValues.add(v);
					} //end if
				} //end switch
			}); //end forEach
			
			//Trim statements
			updateStatement.replace(updateStatement.length()-1, updateStatement.length(), "")
			.append(" WHERE ");
			whereStatement.replace(whereStatement.length()-5, whereStatement.length(), "");	
			sqlValues.addAll(whereValues);
						
			try {
				conn.executePreparedStatement(
						sql + updateStatement + whereStatement, sqlValues.toArray());
				sqlValues.clear();
				whereValues.clear();
				updateStatement.delete(0, updateStatement.length());
				updateStatement.append("");
				whereStatement.delete(0, whereStatement.length());
				whereStatement.append("");
			} catch (DataIntegrityViolationException e) {
				throw new TTSCException("There was an error inserting the following: " + 
						StringUtils.collectionToCommaDelimitedString(sqlValues), e);
			} //end try-catch block
		} //end for
	} //end updateRecords
} //end TableToSQLConverter class


