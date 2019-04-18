package com.brent.databasemanager.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
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

import com.brent.databasemanager.controllers.FileUpload;
import com.brent.databasemanager.services.ConnectionManager;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ConfirmUploadTableTest {
	@LocalServerPort
	private int port;
	
	@Autowired
	private FileUpload uploadController;
	
	@Autowired
	private TestRestTemplate trt;
	
	private static String fileName; 
	private static String fileName2;
	
	private static Map<String, String> tableMap;
	private static Map<String, String> tableMap2;
	private static Map<String, String> badTableColumnNameMap;
	private static Map<String, String> badTableColumnTypeMap;
	private static ArrayList<String> tablesToDrop;
	
	@BeforeClass
	public static void setUp() {
		//fileName = "1537996397501.ser";
		fileName = "1545682840528.ser";
		fileName2 = "1539211107121.ser";
		tableMap = new HashMap<String, String>();
		tableMap2 = new HashMap<String, String>();
		badTableColumnNameMap = new HashMap<String, String>();
		badTableColumnTypeMap = new HashMap<String, String>();
		tablesToDrop = new ArrayList<String>();
//		tableMap.put("Clan", "Text");
//		tableMap.put("Carte", "Text");
//		tableMap.put("Nombre", "Number");
//		tableMap.put("Nombrevar1", "4");
//		tableMap.put("Nombrevar2", "0");
//		tableMap.put("PrixUnitaire", "Number");
//		tableMap.put("PrixUnitairevar1", "10");
//		tableMap.put("PrixUnitairevar2", "0");
//		tableMap.put("PrixTotal", "Number");
//		tableMap.put("PrixTotalvar1", "10");
//		tableMap.put("PrixTotalvar2", "0");
		tableMap.put("zipcode", "Number");
		tableMap.put("zipcodevar1", "10");
		tableMap.put("zipcodevar2", "0");
		tableMap.put("state", "Text");
		tableMap.put("county_code", "Number");
		tableMap.put("county_codevar1", "10");
		tableMap.put("county_codevar2", "0");
		tableMap.put("name", "Text");
		tableMap.put("rate_area", "Number");
		tableMap.put("rate_areavar1", "10");
		tableMap.put("rate_areavar2", "0");
		tableMap2.put("id", "Number");
		tableMap2.put("idvar2", "10");
		tableMap2.put("idvar2", "0");
		tableMap2.put("first_name", "Text");
		tableMap2.put("last_name", "Text");
		tableMap2.put("join_date", "Date");
		tableMap2.put("join_datevar1", "M/d/yyyy");
		tableMap2.put("last_login", "DTime");
		tableMap2.put("last_loginvar1", "YYYY-MM-DDTHH24:MI:SS");
		tableMap2.put("account_balance", "Number");
		tableMap2.put("account_balancevar1", "10");
		tableMap2.put("account_balancevar2", "2");
		badTableColumnNameMap.put("Clan", "Text");
		badTableColumnNameMap.put("Car-t:e", "Text");
		badTableColumnNameMap.put("Nombre", "Number");
		badTableColumnNameMap.put("Nombrevar1", "4");
		badTableColumnNameMap.put("Nombrevar2", "0");
		badTableColumnNameMap.put("PrixUnitaire", "Number");
		badTableColumnNameMap.put("PrixUnitairevar1", "10");
		badTableColumnNameMap.put("PrixUnitairevar2", "0");
		badTableColumnNameMap.put("PrixTotal", "Number");
		badTableColumnNameMap.put("PrixTotalvar1", "10");
		badTableColumnNameMap.put("PrixTotalvar2", "0");
		badTableColumnTypeMap.put("Clan", "Ha\'ck");
		badTableColumnTypeMap.put("Carte", "Text");
		badTableColumnTypeMap.put("Nombre", "Number");
		badTableColumnTypeMap.put("Nombrevar1", "4");
		badTableColumnTypeMap.put("Nombrevar2", "0");
		badTableColumnTypeMap.put("PrixUnitaire", "Number");
		badTableColumnTypeMap.put("PrixUnitairevar1", "10");
		badTableColumnTypeMap.put("PrixUnitairevar2", "0");
		badTableColumnTypeMap.put("PrixTotal", "Number");
		badTableColumnTypeMap.put("PrixTotalvar1", "10");
		badTableColumnTypeMap.put("PrixTotalvar2", "0");
	} //end setUp
	
	@AfterClass
	public static void cleanUp() {
		ConnectionManager conn = new ConnectionManager();
		try {
		System.out.println("Called cleanup");
		conn.getConnection("localhost:1521/xe", "DBADMIN", "p4ssw0rd");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		for(String s:tablesToDrop) {
			try {
				System.out.println("Running");
				conn.executeString("DROP TABLE " + s);
				System.out.println("Ran");
			} catch (Throwable e) {
				e.printStackTrace();
				continue;
			} //end try-catch
		} //end for
	} //end cleanUp
	
	@Test
	public void contextLoads() throws Exception {
		assertThat(uploadController).isNotNull();
	}
	
	@Test
	public void validFileWithRowReturnsSuccess() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("fileins", fileName);
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "TestRowSuccessTable");
		parameters.add("tablerow", true);
		parameters.add("tableColMap", tableMap);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/confirmUploadNewTable", parameters, String.class))
		.contains("success");
		tablesToDrop.add("TestRowSuccessTable");
	} //end validFileWithRowReturnsSuccess
	
	@Test
	public void validFileWithoutRowReturnsSuccess() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("fileins", fileName);
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		//parameters.add("tablename", "TestNoRowSuccessTable");
		parameters.add("tablename", "Zips");
		parameters.add("tablerow", false);
		parameters.add("tableColMap", tableMap);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/confirmUploadNewTable", parameters, String.class))
		.contains("success");
		//tablesToDrop.add("TestNoRowSuccessTable");
	} //end validFileWithoutRowReturnsSuccess
	
	@Test
	public void validDecimalDateDTimeReturnsSuccess() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("fileins", fileName2);
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "TestDecimalDateDTimeTable");
		parameters.add("tablerow", false);
		parameters.add("tableColMap", tableMap2);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/confirmUploadNewTable", parameters, String.class))
		.contains("success");
		tablesToDrop.add("TestDecimalDateDTimeTable");
	} //end validDecimalDateDTimeReturnsSuccess
	
	@Test
	public void invalidURLReturnsFail() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("fileins", fileName);
		parameters.add("dburl", "notreal:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "BadTestTable1");
		parameters.add("tablerow", false);
		parameters.add("tableColMap", tableMap);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/confirmUploadNewTable", parameters, String.class))
		.contains("Connection to target db failed.");
		tablesToDrop.add("BadTestTable1");
	} //end invalidURLReturnsFail
	
	@Test
	public void invalidCredentialsReturnsFail() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("fileins", fileName);
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "baduser");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "BadTestTable2");
		parameters.add("tablerow", false);
		parameters.add("tableColMap", tableMap);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/confirmUploadNewTable", parameters, String.class))
		.contains("credentials");
		tablesToDrop.add("BadTestTable2");
	} //end invalidCredentialsReturnsFail
	
	@Test
	public void invalidTableNameReturnsFail() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("fileins", fileName);
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "Bad-Test/Table:3");
		parameters.add("tablerow", false);
		parameters.add("tableColMap", tableMap);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/confirmUploadNewTable", parameters, String.class))
		.contains("table name");
		tablesToDrop.add("Bad-Test/Table:3");
	} //end invalidTableNameReturnsFail
	
	@Test
	public void invalidTableColumnNameReturnsFail() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("fileins", fileName);
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "BadTestTable4");
		parameters.add("tablerow", false);
		parameters.add("tableColMap", badTableColumnNameMap);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/confirmUploadNewTable", parameters, String.class))
		.contains("success");
		tablesToDrop.add("BadTestTable4");
	} //end invalidTableColumnNameReturnsFail
	
	@Test
	public void invalidTableColumnTypeReturnsFail() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("fileins", fileName);
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBADMIN");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tablename", "BadTestTable5");
		parameters.add("tablerow", false);
		parameters.add("tableColMap", badTableColumnTypeMap);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/confirmUploadNewTable", parameters, String.class))
		.contains("cannot be accepted");
		tablesToDrop.add("BadTestTable5");
	} //end invalidTableColumnTypeReturnsFail
} //end ConfirmUploadTableTest
