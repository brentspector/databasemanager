package com.brent.databasemanager.services;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ConnectionManager {
	
	private JdbcTemplate userJdbcTemplate;
	
	public boolean getConnection(String URL, String user, String pass) {
		userJdbcTemplate = new JdbcTemplate(getDataSource(URL, user, pass));
		return (userJdbcTemplate != null);
	} //end getConnection
	
	public DatabaseMetaData getMetaData() throws SQLException {
		return userJdbcTemplate.getDataSource().getConnection().getMetaData();
	} //end getMetaData
	
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
