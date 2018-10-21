package com.brent.databasemanager.services;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brent.databasemanager.pojo.TableContents;
import com.brent.databasemanager.pojo.TableReadException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TableReader {
	
	@Autowired
	ConnectionManager conn;
	
	@Autowired
	ObjectMapper objmap;
	
	public String pullTableData(String url, String user, String pass, String tableList) {
		try {
			//Verify connection
			conn.getConnection(url, user, pass); 
			
			//Convert tables to list
			TypeReference<List<String>> jsonType = new TypeReference<List<String>>() {};
			List<String> tables = objmap.readValue(tableList, jsonType);
			
			//Container for each table object
			ArrayList<TableContents> tc = new ArrayList<TableContents>();
			
			//Convert table data into TableContents
			ResultSet res;
			DatabaseMetaData meta = conn.getMetaData();
			for(String s:tables) {
				res = meta.getTables(null, null, s.toUpperCase(), new String[] {"TABLE"});
				if(!res.next())
				{
					throw new TableReadException("Table " + s + " does not exist in target database. "
							+ "Read cancelled.");
				} //end if
				res.close();
				List<Map<String, Object>> tableData = conn.queryMultipleRows("select * from " + s, new Object[] {});
				ArrayList<Map<String,String>> newTableData = new ArrayList<Map<String, String>>();
				HashSet<String> tableHeaders = new HashSet<String>();
				for(Map<String, Object>m:tableData) {
					tableHeaders.addAll(m.keySet());
					LinkedHashMap<String, String> newMap = new LinkedHashMap<String, String>(); 
					for(String key:m.keySet()) {
						newMap.put(key, String.valueOf(m.get(key)));
					} //end key for
					newTableData.add(newMap);
				} //end map for
				
				//Assign values to new TableContents
				TableContents tableContents = new TableContents();
				tableContents.setName(s);
				tableContents.getHeaders().addAll(tableHeaders);
				tableContents.setContents(newTableData);
				tc.add(tableContents);
			} //end for
			
			//Return list of table data as JSON
			return objmap.writeValueAsString(tc);
		} catch (SQLRecoverableException e) {
			throw new TableReadException("Connection to target db failed. Please verify connection details.");
		} catch (SQLException e) {
			throw new TableReadException("Error getting database details.", e);
		} catch (JsonProcessingException e) {
			throw new TableReadException("Error while converting to JSON.",e);
		} catch (IOException e) {
			throw new TableReadException("Unable to convert list of tables.", e);
		} //end try-catch block
	} //end tableData

	public String pullTableList(String url, String user, String pass) {
		try {
			//Verify connection
			conn.getConnection(url, user, pass);
			
			//Get database list
			List<Map<String, Object>> tableResult = conn.queryMultipleRows("select table_name from user_tables", new Object[] {});
			ArrayList<Object> tableList = new ArrayList<Object>();
			for(Map<String, Object> m:tableResult) {
				tableList.addAll(m.values());
			} //end for

			return objmap.writeValueAsString(tableList);
		} catch (SQLRecoverableException e) {
			throw new TableReadException("Connection to target db failed. Please verify connection details.");
		} catch (SQLException e) {
			throw new TableReadException("Error getting database details.", e);
		} catch (JsonProcessingException e) {
			throw new TableReadException("Error while mapping JSON.", e);
		} //end try-catch block
	} //end pullTableList
} //end TableReader
