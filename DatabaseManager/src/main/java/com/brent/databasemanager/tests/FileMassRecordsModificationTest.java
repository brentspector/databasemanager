package com.brent.databasemanager.tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.brent.databasemanager.controllers.ManageRecord;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class FileMassRecordsModificationTest {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	ManageRecord mr;
	
	@Autowired
	private TestRestTemplate trt;
	
	@BeforeClass
	public static void setUp() {
		
	} //end setUp
	
	@Test
	public void contextLoads() throws Exception {
		assertThat(mr).isNotNull();
	} //end contextLoads
	
	@Test
	public void addValidRecordsWithCSVFileReturnsSuccess() {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "CUSTOMERS");
		parameters.add("file", new ClassPathResource("static/MassFileAdd.csv"));
		parameters.add("fileType", "AUTO");
		assertThat(this.trt.postForObject("http://localhost:" + port + "/massFileAddRecords", parameters, String.class))
		.contains("successfully");
	} //end addValidRecordsWithCSVFileReturnsSuccess
	
	@Test
	public void addValidRecordsWithINIFileReturnsSuccess() {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "CUSTOMERS");
		parameters.add("file", new ClassPathResource("static/MassFileAdd.ini"));
		parameters.add("fileType", "AUTO");
		assertThat(this.trt.postForObject("http://localhost:" + port + "/massFileAddRecords", parameters, String.class))
		.contains("successfully");
	} //end addValidRecordsWithINIFileReturnsSuccess
	
	@Test
	public void addValidRecordsWithAmbiguousINIFileReturnsSuccess() {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "CUSTOMERS");
		parameters.add("file", new ClassPathResource("static/AmbiguousMassFileAddINI.txt"));
		parameters.add("fileType", "INI");
		assertThat(this.trt.postForObject("http://localhost:" + port + "/massFileAddRecords", parameters, String.class))
		.contains("successfully");
	} //end addValidRecordsWithAmbiguousINIFileReturnsSuccess
	
	@Test
	public void addValidRecordsWithAmbiguousCSVFileReturnsSuccess() {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "CUSTOMERS");
		parameters.add("file", new ClassPathResource("static/AmbiguousMassFileAddCSV.txt"));
		parameters.add("fileType", "CSV");
		assertThat(this.trt.postForObject("http://localhost:" + port + "/massFileAddRecords", parameters, String.class))
		.contains("successfully");
	} //end addValidRecordsWithAmbiguousCSVFileReturnsSuccess
	
	@Test
	public void editValidRecordsWithFileReturnSuccess() {
		
	} //end editValidRecordsWithFileReturnSuccess
	
	@Test
	public void deleteValidRecordsWithFileReturnsSuccess() {
		
	} //end deleteValidRecordsWithFileReturnsSuccess
	
	@Test
	public void addInvalidDataWithFileReturnsFail() {
		
	} //end addInvalidDataWithFileReturnsFail
	
	@Test
	public void addConflictingDataWithFileReturnsFail() {
		
	} //end addConflictingDataWithFileReturnsFail
	
	@Test
	public void addMissingRequiredColumnWithFileReturnsFail() {
		
	} //end addMissingRequiredColumnWithFileReturnsFail
	
	@Test
	public void addTooManyColumnsWithFileReturnsFail() {
		
	} //end addTooManyColumnsWithFileReturnsFail
	
	@Test
	public void editInvalidDataWithFileReturnsFail() {
		
	} //end editInvalidDataWithFileReturnsFail
	
	@Test
	public void editMissingRequiredColumnWithFileReturnsFail() {
		
	} //end editMissingRequiredColumnWithFileReturnsFail
	
	@Test
	public void editTooManyColumnsWithFileReturnsFail() {
		
	} //end editTooManyColumnsWithFileReturnsFail
	
	@Test
	public void deleteInvalidDataWithFileReturnsFail() {
		
	} //end deleteInvalidDataWithFileReturnsFail
	
	@Test
	public void deleteTooManyColumnsWithFileReturnsFail() {
		
	} //end deleteTooManyColumnsWithFileReturnsFail
} //end FileMassRecordsModificationTest
