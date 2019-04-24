package com.brent.databasemanager.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.brent.databasemanager.DatabaseManagerApplication;
import com.brent.databasemanager.pojo.FileParseException;
import com.brent.databasemanager.pojo.TableContents;
import com.brent.databasemanager.pojo.UploadedFile;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FileParse {
	
	//Logger
	private static final Logger log = LoggerFactory.getLogger(DatabaseManagerApplication.class);
	
	//Holds file parsing results for JSON marshalling
	@Autowired
	UploadedFile uFile;
	
	//Holds formatted file contents for table creation
	@Autowired
	private TableContents tc;
	
	//Formats content
	@Autowired
	StringToTableFormatter sttf;
	
	//Upload to S3
	@Autowired
	S3Manager s3;
	
	//Convert to JSON
	@Autowired
	ObjectMapper objmap;
	
	//Parses a given file as new table and new records
	public String[] parseNewTableUpload(MultipartFile file, String type) {
		try {
			String fileType = parseType(file.getOriginalFilename(), type);
			switch(fileType) {
			case "CSV":
				uFile.setContents(parseContents(file));
				tc = sttf.formatCSVContents(uFile.getContents());
				return new String[] {objmap.writeValueAsString(tc), s3.UploadFile(tc)};
			case "INI":
				uFile.setContents(parseContents(file));
				tc = sttf.formatINIContents(uFile.getContents());
				return new String[] {objmap.writeValueAsString(tc), s3.UploadFile(tc)};
			case "TXT":
				throw new FileParseException("To upload TXT documents, please specify the format type");
			default:
				throw new FileParseException("Your file " + fileType + " is in a format that is not supported");
			} //end switch		
		} catch (IOException e) {
			throw new FileParseException("The file provided encountered input issues", e);
		} //end try-catch block
	} // end parseNewTableUpload method
	
	//Parses a given file and returns records
	public TableContents parseNewTableContents(MultipartFile file, String type) {
		try {
			String fileType = parseType(file.getOriginalFilename(), type);
			switch(fileType) {
			case "CSV":
				uFile.setContents(parseContents(file));
				tc = sttf.formatCSVContents(uFile.getContents());
				return tc;
			case "INI":
				uFile.setContents(parseContents(file));
				tc = sttf.formatINIContents(uFile.getContents());
				return tc;
			case "TXT":
				throw new FileParseException("To upload TXT documents, please specify the format type");
			default:
				throw new FileParseException("Your file " + fileType + " is in a format that is not supported");
			} //end switch		
		} catch (IOException e) {
			throw new FileParseException("The file provided encountered input issues", e);
		} //end try-catch block
	} //end parseNewtableContents

	//Gets file type based on submitted file
	private String parseType(String fileName, String type) 
	{
		//Get type of upload
        if(type.equals("AUTO"))
        {
	       	return fileName.substring(fileName.lastIndexOf(".") + 1).trim().toUpperCase();
        } //end if
        else
        {
        	return type;
        } //end else		
	} //end parseType method
	
	//Reads file into string
	private String parseContents (MultipartFile file) throws IOException 
	{ 
		//Store file contents
		String contents = "";
		
	    // Create components to read the file   
	    StringWriter writer = new StringWriter();
	    InputStream filecontent = null;
	    
	    //Parse given file
	    try {
	    	//Get contents
	        filecontent = file.getInputStream();
	        IOUtils.copy(filecontent, writer, StandardCharsets.UTF_8);
	        contents = writer.toString();
	        
	        //Return contents
	        return contents;
	    } catch (FileNotFoundException fne) {
	    	throw new FileParseException("The file provided is either non-existent or protected", fne);
	    } finally {
	        if (filecontent != null) {
	            filecontent.close();
	        } //end if
	    } // end try-catch-finally
	} //end parseContents method
} //FileParse class
