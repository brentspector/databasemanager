package com.brent.databasemanager.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.brent.databasemanager.services.FileParse;
import com.brent.databasemanager.services.TableToSQLConverter;

@RestController
public class FileUpload {
	
	@Autowired
	FileParse fp;
	
	@Autowired
	TableToSQLConverter tsc;
	
	@PostMapping("/uploadNewTable")
	public String uploadNewTableFile(@RequestParam("file") MultipartFile file, 
			@RequestParam("type") String type, HttpServletResponse resp) 
	{
		String[] temp = fp.parseNewTableUpload(file, type);
		resp.addHeader("fileins", temp[1]);
		return temp[0];
	} //end uploadNewTableFile

	@PostMapping("/confirmUploadNewTable")
	public String confirmUploadNewTable(@RequestParam("fileins") String fileInstance,
			@RequestParam("dburl") String url, @RequestParam("dbuser") String user,
			@RequestParam("dbpass") String pass, @RequestParam("tablename") String tableName,
			@RequestParam("tablerow") boolean rowIndexing, @RequestParam("tableColMap") String tableColumns)
	{
		return tsc.constructTable(url, user, pass, fileInstance, tableName, rowIndexing, tableColumns);
	} //end confirmUploadNewTable

} //end FileUpload class
