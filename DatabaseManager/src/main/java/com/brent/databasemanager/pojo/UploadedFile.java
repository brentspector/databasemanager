package com.brent.databasemanager.pojo;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class UploadedFile {
	
	private String contents;
	private String type;

}
