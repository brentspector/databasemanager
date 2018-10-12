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
import com.brent.databasemanager.pojo.TTSCException;
import com.brent.databasemanager.pojo.TableContents;
import com.fasterxml.jackson.core.JsonProcessingException;
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
		} //end try-catch block
	} //end constructTable

	private void validateTable(String tableName, DatabaseMetaData meta) throws SQLException
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
			throw new TTSCException("Table " + tableName + " already exists in target database. "
					+ "Creation cancelled.");
		} //end if
		res.close();
	} //end validateTable	
	
	private void createTable(String tableName, Map<String, String> dateFormats, 
			Map<String, String> timestampFormats, Map<String, String> columnTypes, 
			ArrayList<String> columnNames, boolean rowIndexing, Map tableColumnMapping) throws SQLException
	{
		String sql;
		
		//Validate table name
		log.warn("Before validate");
		validateTable(tableName, conn.getMetaData());
		
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
			Map<String, String> timestampFormats, String sql, boolean row)
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
} //end TableToSQLConverter class
