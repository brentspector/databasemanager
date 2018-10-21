package com.brent.databasemanager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.brent.databasemanager.services.TableReader;

@RestController
public class TableRead {
	
	@Autowired
	TableReader tr;
	
	@PostMapping("/read")
	public String getTableList(@RequestParam("dburl") String url, 
			@RequestParam("dbuser") String user, @RequestParam("dbpass") String pass) {
		return tr.pullTableList(url, user, pass);
	} //end getTableList
	
	@PostMapping("/readTables")
	public String getTableData(@RequestParam("dburl") String url, 
			@RequestParam("dbuser") String user, @RequestParam("dbpass") String pass,
			@RequestParam("tableList") String tableList) {
		return tr.pullTableData(url, user, pass, tableList);
	} //end getTableData
} //end TableRead class
