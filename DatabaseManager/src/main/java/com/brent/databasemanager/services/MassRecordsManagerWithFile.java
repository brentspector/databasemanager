package com.brent.databasemanager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.brent.databasemanager.pojo.APIResponse;
import com.brent.databasemanager.pojo.RecordManagerException;
import com.brent.databasemanager.pojo.TableContents;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MassRecordsManagerWithFile {	
	@Autowired
	FileParse fp;
	
	@Autowired
	TableToSQLConverter ttsc;
	
	@Autowired
	ObjectMapper objmap;
	
	public String addRecordsToTable(String url, String user, String pass, String tableName, MultipartFile records, String fileType) {		
		//Parse the file
		TableContents tc = fp.parseNewTableContents(records, fileType);
		System.out.println("Parsed");	
		//Add contents to table
		return ttsc.addRecords(url, user, pass, tableName, tc);			
	} //end addRecordsToTable
	
	public String editRecordsInTable(String url, String user, String pass, String tableName, MultipartFile records, String fileType, 
			String primaryKeyList) {
		//Parse the file
		TableContents tc = fp.parseNewTableContents(records, fileType);
		
		//Edit records in table
		return ttsc.editRecords(url, user, pass, tableName, tc, primaryKeyList);
	} //end editRecordsInTable
	
	public String deleteRecordsFromTable(String url, String user, String pass, String tableName, MultipartFile records, String fileType) {
		//Parse the file
		TableContents tc = fp.parseNewTableContents(records, fileType);
		
		//Delete records in table
		return ttsc.deleteRecords(url, user, pass, tableName, tc);
	} //end deleteRecordsFromTable
} //end MassRecordsManagerWithFile
