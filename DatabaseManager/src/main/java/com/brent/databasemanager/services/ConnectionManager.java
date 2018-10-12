package com.brent.databasemanager.services;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
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

	public void executeString(String sql) {
		userJdbcTemplate.execute(sql);		
	} //end executeString

	public void executePreparedStatement(String sql, Object[] sqlValues) {
		userJdbcTemplate.update(sql, sqlValues);
	} //end executePreparedStatement
} //end ConnectionVerifier class
