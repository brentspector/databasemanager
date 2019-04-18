package com.brent.databasemanager.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MassRecordsManagerWithFile {
	public String addRecordsToTable(String url, String user, String pass, String tableName, MultipartFile records) {
		return "";
	} //end addRecordsToTable
	
	public String editRecordsInTable(String url, String user, String pass, String tableName, MultipartFile records) {
		return "";
	} //end editRecordsInTable
	
	public String deleteRecordsFromTable(String url, String user, String pass, String tableName, MultipartFile records) {
		return "";
	} //end deleteRecordsFromTable
} //end MassRecordsManagerWithFile
