package com.brent.databasemanager.services;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.zaxxer.hikari.HikariDataSource;

@Service
public class ConnectionManager {
	
	private JdbcTemplate userJdbcTemplate;
	
	public boolean getConnection(String URL, String user, String pass) {
		userJdbcTemplate = new JdbcTemplate(getDataSource(URL, user, pass));
		return (userJdbcTemplate != null);
	} //end getConnection
	
	public boolean endConnection() throws SQLException {
		try {
			userJdbcTemplate.getDataSource().unwrap(HikariDataSource.class).close();
		} catch (SQLException e) {
			throw (SQLException) e.getCause();
		} //end try-catch block
		return true;
	} //end endConnection
	
	public DatabaseMetaData getMetaData() throws SQLException {
		return userJdbcTemplate.getDataSource().getConnection().getMetaData();
	} //end getMetaData
	
	public Map<String, Integer> getTableColumnTypes(String tableName) throws SQLException {
		ResultSet res = getMetaData().getColumns(null, null, tableName, "%");
		HashMap<String, Integer> columnMapping = new HashMap<String, Integer>();
		while(res.next()) {
			columnMapping.put(res.getString("COLUMN_NAME"), res.getInt("DATA_TYPE"));			
		} //end while
		
		return columnMapping;
	} //end getTableColumnTypes
	
	public List<String> getRecordIDColumn(String tableName) throws SQLException {
		ResultSet res = getMetaData().getBestRowIdentifier(null, null, tableName.toUpperCase(), 2, true);
		ArrayList<String> columnMapping = new ArrayList<String>();
		while(res.next()) {
			columnMapping.add(res.getString("COLUMN_NAME"));			
		} //end while
		
		return columnMapping;
	} //end getRecordIDColumn
	
	private DataSource getDataSource(String url, String user, String password) {
	    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
	    dataSourceBuilder.url("jdbc:oracle:thin:@" + url);
	    dataSourceBuilder.username(user);
	    dataSourceBuilder.password(password);
	    return dataSourceBuilder.build();   
	} //end getDataSource

	public void executeString(String sql) throws SQLException {
		userJdbcTemplate.execute(sql);	
	} //end executeString

	public void executePreparedStatement(String sql, Object[] sqlValues) throws SQLException {
		System.out.println(sql);
		System.out.println(Arrays.toString(sqlValues));
		userJdbcTemplate.update(sql, sqlValues);
	} //end executePreparedStatement
	
	public List<Map<String, Object>> queryMultipleRows(String sql, Object[] sqlValues) throws SQLException {
		try {
			return userJdbcTemplate.queryForList(sql, sqlValues);
		} catch (CannotGetJdbcConnectionException e) {
			if (e.contains(SQLRecoverableException.class)) {
				throw (SQLRecoverableException) e.getCause();
			} //end if
			
			throw (SQLException) e.getCause();
		} //end try-catch block
	} //end queryPreparedStatement
} //end ConnectionVerifier class
