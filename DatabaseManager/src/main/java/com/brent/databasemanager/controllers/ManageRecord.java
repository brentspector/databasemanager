package com.brent.databasemanager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.brent.databasemanager.services.MassRecordsManagerWithColumn;
import com.brent.databasemanager.services.MassRecordsManagerWithFile;
import com.brent.databasemanager.services.SingleRecordManager;

@RestController
public class ManageRecord {
	@Autowired
	SingleRecordManager srm;
	
	@Autowired
	MassRecordsManagerWithFile mrmf;
	
	@Autowired
	MassRecordsManagerWithColumn mrmc;
	
	@PostMapping("/editRecord")
	public String editTableRecord(@RequestParam("dburl") String url, @RequestParam("dbuser") String user, 
			@RequestParam("dbpass") String pass, @RequestParam("tablename") String tableName,
			@RequestParam("keys") String primaryKeyList, @RequestParam("record") String recordData) {
		return srm.editRecordInTable(url, user, pass, tableName, primaryKeyList, recordData);
	} //end editTableRecord
	
	@PostMapping("/deleteRecord")
	public String deleteTableRecord(@RequestParam("dburl") String url, @RequestParam("dbuser") String user, 
			@RequestParam("dbpass") String pass, @RequestParam("tablename") String tableName,
			@RequestParam("record") String recordData) {
		return srm.deleteRecordFromTable(url, user, pass, tableName, recordData);
	} //end deleteTableRecord
	
	@PostMapping("/addRecord")
	public String addTableRecord(@RequestParam("dburl") String url, @RequestParam("dbuser") String user, 
			@RequestParam("dbpass") String pass, @RequestParam("tablename") String tableName,
			@RequestParam("record") String recordData) {
		return srm.addRecordToTable(url, user, pass, tableName, recordData);
	} //end addTableRecord
	
	@PostMapping("/massFileEditRecords")
	public String massFileEditTableRecords(@RequestParam("dburl") String url, @RequestParam("dbuser") String user, 
			@RequestParam("dbpass") String pass, @RequestParam("tablename") String tableName,
			@RequestParam("file") MultipartFile records, @RequestParam("fileType") String fileType) {
		return mrmf.editRecordsInTable(url, user, pass, tableName, records, fileType);
	} //end massFileEditTableRecords
	
	@PostMapping("/massFileDeleteRecords")
	public String massFileDeleteTableRecords(@RequestParam("dburl") String url, @RequestParam("dbuser") String user, 
			@RequestParam("dbpass") String pass, @RequestParam("tablename") String tableName,
			@RequestParam("file") MultipartFile records, @RequestParam("fileType") String fileType) {
		return mrmf.deleteRecordsFromTable(url, user, pass, tableName, records, fileType);
	} //end massFileDeleteTableRecords
	
	@PostMapping("/massFileAddRecords")
	public String massFileAddTableRecords(@RequestParam("dburl") String url, @RequestParam("dbuser") String user, 
			@RequestParam("dbpass") String pass, @RequestParam("tablename") String tableName,
			@RequestParam("file") MultipartFile records, @RequestParam("fileType") String fileType) {
		return mrmf.addRecordsToTable(url, user, pass, tableName, records, fileType);
	} //end massFileAddTableRecords
	
	@PostMapping("/massColumnEditRecords")
	public String massColumnEditTableRecords(@RequestParam("dburl") String url, @RequestParam("dbuser") String user, 
			@RequestParam("dbpass") String pass, @RequestParam("tablename") String tableName, @RequestParam("column") String column, 
			@RequestParam("value") String value) {
		return mrmc.editRecordsInTable(url, user, pass, tableName, column, value);
	} //end massColumnEditTableRecords
	
	@PostMapping("/massColumnDeleteRecords")
	public String massColumnDeleteTableRecords(@RequestParam("dburl") String url, @RequestParam("dbuser") String user, 
			@RequestParam("dbpass") String pass, @RequestParam("tablename") String tableName, @RequestParam("column") String column) {
		return mrmc.deleteRecordsFromTable(url, user, pass, tableName, column);
	} //end massColumnDeleteTableRecords
} //end ManageRecord