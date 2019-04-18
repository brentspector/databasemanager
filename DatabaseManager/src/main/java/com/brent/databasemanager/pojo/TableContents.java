package com.brent.databasemanager.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class TableContents implements Serializable {

	private String name = "";
	private ArrayList<String> headers = new ArrayList<String>();
	private ArrayList<String> keys = new ArrayList<String>();
	private ArrayList<Map<String,String>> contents = new ArrayList<Map<String,String>>();
	
	public void clear()
	{
		headers.clear();
		keys.clear();
		contents.clear();
	} //end clear
}
