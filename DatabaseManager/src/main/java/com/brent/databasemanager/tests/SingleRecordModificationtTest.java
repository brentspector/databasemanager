package com.brent.databasemanager.tests;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.brent.databasemanager.controllers.ManageRecord;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SingleRecordModificationtTest {

	@LocalServerPort
	private int port;
	
	@Autowired
	private ManageRecord mr;
	
	@Autowired
	private TestRestTemplate trt;
	
	private static Map<String, String> validAddRecord;
	private static Map<String, String> validPrimaryKeys;
	private static Map<String, String> validEditRecord;
	private static Map<String, String> validDeleteRecord;
	private static Map<String, String> invalidTypeRecord;
	private static Map<String, String> violateUniqueAddRecord;
	private static Map<String, String> nonExistantColumnAddRecord;
	private static Map<String, String> missingRequiredRecord;
	private static Map<String, String> nonExistantRecordKey;
	private static Map<String, String> multipleFoundRecord;
	private static Map<String, String> multipleResultPrimaryKey;
	private static Map<String, String> nonExistantColumnEditDeleteRecord;
	private static Map<String, String> nonExistantDeleteRecord;
	
	@BeforeClass
	public static void setUp() {
		validAddRecord = new HashMap<String, String>();
		validPrimaryKeys = new HashMap<String, String>();
		validEditRecord = new HashMap<String, String>();
		validDeleteRecord = new HashMap<String, String>();
		invalidTypeRecord = new HashMap<String, String>();
		violateUniqueAddRecord = new HashMap<String, String>();
		nonExistantColumnAddRecord = new HashMap<String, String>();
		missingRequiredRecord = new HashMap<String, String>();
		nonExistantRecordKey = new HashMap<String, String>();
		multipleFoundRecord = new HashMap<String, String>();
		multipleResultPrimaryKey = new HashMap<String, String>();
		nonExistantColumnEditDeleteRecord = new HashMap<String, String>();
		nonExistantDeleteRecord = new HashMap<String, String>();
		validAddRecord.put("ID", "4");
		validAddRecord.put("FIRST_NAME", "Henry");
		validAddRecord.put("LAST_NAME", "McGreggor");
		validPrimaryKeys.put("FIRST_NAME", "KCube");
		validEditRecord.put("ID", "2");
		validEditRecord.put("FIRST_NAME", "KCube");
		validEditRecord.put("LAST_NAME", "JoManDo");
		validDeleteRecord.put("ID", "4");
		validDeleteRecord.put("FIRST_NAME", "Henry");
		validDeleteRecord.put("LAST_NAME", "McGreggor");
		invalidTypeRecord.put("ID", "Four");
		invalidTypeRecord.put("FIRST_NAME", "Henry");
		invalidTypeRecord.put("LAST_NAME", "McGreggor");
		violateUniqueAddRecord.put("YEAR", "1993");
		violateUniqueAddRecord.put("TITLE", "BadTrack");
		nonExistantColumnAddRecord.put("ID", "4");
		nonExistantColumnAddRecord.put("FIRST_NAME", "Henry");
		nonExistantColumnAddRecord.put("LAST_NAME", "McGreggor");
		nonExistantColumnAddRecord.put("ORGANIZATION", "BadValue");
		missingRequiredRecord.put("ID", "");
		missingRequiredRecord.put("TITLE", "MissedValue");
		nonExistantRecordKey.put("FIRST_NAME", "BadValue");
		multipleFoundRecord.put("ID", "22");
		multipleFoundRecord.put("FIRST_NAME", "Blue");
		multipleFoundRecord.put("LAST_NAME", "Blugers");
		multipleResultPrimaryKey.put("FIRST_NAME", "Blue");
		nonExistantColumnEditDeleteRecord.put("ID", "3");
		nonExistantColumnEditDeleteRecord.put("FIRST_NAME", "Lriche");
		nonExistantColumnEditDeleteRecord.put("LAST_NAME", "Jack");
		nonExistantColumnEditDeleteRecord.put("ORGANIZATION", "BadValue");
		nonExistantDeleteRecord.put("ID", "999");
		nonExistantDeleteRecord.put("FIRST_NAME", "Gogo");
		nonExistantDeleteRecord.put("LAST_NAME", "Life");
	}
	
	@Test
	public void contextLoads() throws Exception {
		assertThat(mr).isNotNull();
	} //end contextLoads
	
	@Test
	public void addValidRecordReturnsSuccess() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "CUSTOMERS");
		parameters.add("record", validAddRecord);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/addRecord", parameters, String.class))
		.contains("successfully");
	} //end addValidRecordReturnsSuccess
	
	@Test
	public void editRecordWithValidDataReturnsSuccess() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "CUSTOMERS");
		parameters.add("keys", validPrimaryKeys);
		parameters.add("record", validEditRecord);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/editRecord", parameters, String.class))
		.contains("successfully");
	} //end editRecordWithValidDataReturnsSuccess
	
	@Test
	public void deleteExistingRecordReturnsSuccess() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "CUSTOMERS");
		parameters.add("record", validDeleteRecord);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/deleteRecord", parameters, String.class))
		.contains("successfully");
	} //end deleteExistingRecordReturnsSuccess
	
	@Test
	public void addInvalidDataReturnsFail() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "CUSTOMERS");
		parameters.add("record", invalidTypeRecord);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/addRecord", parameters, String.class))
		.contains("BAD_REQUEST");
	} //end addInvalidDataReturnsFail
	
	@Test
	public void addConflictingDataReturnsFail() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "SONGS");
		parameters.add("record", violateUniqueAddRecord);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/addRecord", parameters, String.class))
		.contains("BAD_REQUEST");
	} //end addConflictingDataReturnsFail
	
	@Test
	public void addMissingRequiredColumnReturnsFail() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "SONGS");
		parameters.add("record", missingRequiredRecord);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/addRecord", parameters, String.class))
		.contains("NOT_FOUND");
	} //end addMissingRequiredColumnReturnsFail
	
	@Test
	public void addTooManyColumnsReturnsFail() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "CUSTOMERS");
		parameters.add("record", nonExistantColumnAddRecord);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/addRecord", parameters, String.class))
		.contains("NOT_FOUND");
	} //end addTooManyColumnsReturnsFail

	@Test
	public void editRecordWithInvalidDataReturnsFail() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "CUSTOMERS");
		parameters.add("keys", validPrimaryKeys);
		parameters.add("record", invalidTypeRecord);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/editRecord", parameters, String.class))
		.contains("BAD_REQUEST");
	} //end editRecordWithInvalidDataReturnsFail
	
	@Test
	public void editNonExistantRecordReturnsFail() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "CUSTOMERS");
		parameters.add("keys", nonExistantRecordKey);
		parameters.add("record", validEditRecord);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/editRecord", parameters, String.class))
		.contains("No records");
	} //end editNonExistantRecordReturnsFail
	
	@Test
	public void editRecordWithMultipleResultsReturnsFail() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "CUSTOMERS");
		parameters.add("keys", multipleResultPrimaryKey);
		parameters.add("record", multipleFoundRecord);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/editRecord", parameters, String.class))
		.contains("many records");
	} //end editRecordWithMultipleResultsReturnsFail
	
	@Test
	public void editRecordMissingRequiredColumnReturnsFail() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "SONGS");
		parameters.add("keys", validPrimaryKeys);
		parameters.add("record", missingRequiredRecord);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/editRecord", parameters, String.class))
		.contains("NOT_FOUND");
	} //end editRecordMissingRequiredColumnReturnsFail
	
	@Test
	public void editRecordTooManyColumnsReturnsFail() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "CUSTOMERS");
		parameters.add("keys", validPrimaryKeys);
		parameters.add("record", nonExistantColumnEditDeleteRecord);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/editRecord", parameters, String.class))
		.contains("NOT_FOUND");
	} //end editRecordTooManyColumnsReturnsFail
	
	@Test
	public void deleteRecordWithInvalidDataReturnsFail() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "CUSTOMERS");
		parameters.add("record", invalidTypeRecord);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/deleteRecord", parameters, String.class))
		.contains("EXPECTATION_FAILED");
	} //end deleteRecordWithInvalidDataReturnsFail
	
	@Test
	public void deleteNonExistantRecordReturnsFail() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "CUSTOMERS");
		parameters.add("record", nonExistantDeleteRecord);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/deleteRecord", parameters, String.class))
		.contains("No records");
	} //end deleteNonExistantRecordReturnsFail
	
	@Test
	public void deleteRecordWithMultipleResultsReturnsFail() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "CUSTOMERS");
		parameters.add("record", multipleFoundRecord);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/deleteRecord", parameters, String.class))
		.contains("many records");
	} //end deleteRecordWithMultipleResultsReturnsFail
	
	@Test
	public void deleteRecordTooManyColumnsReturnsFail() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "CUSTOMERS");
		parameters.add("record", nonExistantColumnEditDeleteRecord);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/deleteRecord", parameters, String.class))
		.contains("NOT_FOUND");
	} //end deleteRecordTooManyColumnsReturnsFail
} //end SingleREcordModificationTest
