package com.brent.databasemanager.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brent.databasemanager.DatabaseManagerApplication;
import com.brent.databasemanager.pojo.STTFException;
import com.brent.databasemanager.pojo.TableContents;

@Service
public class StringToTableFormatter {
	
	//Logger
	private static final Logger log = LoggerFactory.getLogger(DatabaseManagerApplication.class);
	
	@Autowired
	TableContents tc;
	
	//Converts CSV file into table structure
	public TableContents formatCSVContents(String content)
	{
		//Explicitly clear due to memory references
		tc.clear();
		
		//Set character for split
		String splitChar = ",";
		
		//Check if file contains new lines and split char
		if(!content.contains(Character.toString((char)10)))
		{
			throw new STTFException("Bad Format - Each record must have new line");
		} //end if
		else if(!content.contains(splitChar))
		{
			throw new STTFException("STTFErr - Bad Format - Records must contain delimiter");
		} //end else if
		
		//Split content at each new line
		ArrayList<String> contains = new ArrayList<String>(Arrays.asList(
				content.split(Character.toString((char)10))));
		
		//Remove last record if it contains nothing
		if((int)contains.get(contains.size()-1).toCharArray()[0] == 0)
		{
			 contains.remove(contains.size()-1);
		} //end if
		
		//Get the first record and split by delimiter
		String[] headers;
		try {
			headers = contains.get(0).split(splitChar);
		} catch (Exception e) {
			throw new STTFException("Bad Format - Failed to split contents", e);
		} //end try-catch block
		
		//Loop over each item and add it as a column name
		for(int i = 0; i < headers.length; i++)
		{
			headers[i] = headers[i].replaceAll("\\s+", "");
			tc.getHeaders().add(headers[i]);
		} //end for
		
		//Identify and store individual records
		for(int i = 1; i < contains.size(); i++)
		{			
			//Get next record and split at comma
			String[] temp;
			try {
				temp = contains.get(i).split(splitChar);
			} catch (Exception e) {
				throw new STTFException("Bad Format - Failed to split the following: " + contains.get(i) + " at row " + Integer.toString(i), e);
			} //end try-catch block
			
			//Store each record in an array list
			HashMap<String, String> row = new HashMap<String,String>();
			
			//Loop over each item and store as a record
			for(int j = 0; j < temp.length; j++)
			{
				temp[j] = temp[j].replaceAll(Character.toString((char)13), "");
				row.put(headers[j], temp[j]);				
			} //end record for
			
			//Add record to table contents
			tc.getContents().add(row);
		} //end contains for
		
		//Return the object
		return tc;
	} //end formatCSVContents
	
	//Converts INI file into table structure
	public TableContents formatINIContents(String content)
	{
		//Explicitly clear due to memory references
		tc.clear();
		
		//Split string into each line
		ArrayList<String> contains = new ArrayList<String>(Arrays.asList(content.split(Character.toString((char)10))));
		if((int)contains.get(contains.size()-1).toCharArray()[0] == 0)
		{
			 contains.remove(contains.size()-1);
		} //end if
		
		//Loop through and compile header and data lists	
		ArrayList<String> headers = new ArrayList<String>();
		LinkedHashMap<String, LinkedHashMap<String, String>> data = 
				new LinkedHashMap<String, LinkedHashMap<String, String>>();		
		String currentRecordName = "";
		
		//Loop through each line
		for(String s:contains)
		{
			//This row is a property of the current record
			if(s.contains("="))
			{
				String[] keyValue = s.split("=");
				keyValue[1] = keyValue[1].replaceAll(Character.toString((char)13), "");
				if(!headers.contains(keyValue[0]))
				{
					headers.add(keyValue[0]);
				} //end new header if
								
				data.get(currentRecordName).put(keyValue[0], keyValue[1]);
			} //end new property if
			else
			{
				try {
					currentRecordName = s.substring(
							s.indexOf('[') + 1, s.indexOf(']'));
				} catch (Exception e) {
					throw new STTFException("Bad Format - Missing Bracket(s) for the following: " + s, e);
				}//end try-catch block
				data.put(currentRecordName, new LinkedHashMap<String, String>());
			} //end else
		} //end for
		
		//Add each header to table contents
		for(String s:headers)
		{
			tc.getHeaders().add(s);
		} //end for
		
		//Store each record in an array list
		HashMap<String, String> row;
		
		//Loop over each items and add records to table contents
		for(Map.Entry<String, LinkedHashMap<String, String>> e:data.entrySet())
		{
			row = new HashMap<String, String>();
			//row.put("RecordID", e.getKey());
			for(String h:headers)
			{
				row.put(h, e.getValue().get(h));
			} //end header to data for
			
			//Add record to table contents
			tc.getContents().add(row);
		} //end record for
		
		//Add last header
		//tc.getHeaders().add(0, "RecordID");
		
		//Return table contents
		return tc;
	} //end formatINIContents	
} //end StringToTableFormatter class
