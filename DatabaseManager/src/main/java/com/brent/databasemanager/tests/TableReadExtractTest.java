package com.brent.databasemanager.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;

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

import com.brent.databasemanager.controllers.TableRead;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TableReadExtractTest {

	@LocalServerPort
	private int port;
	
	@Autowired
	TableRead readController;
	
	@Autowired
	TestRestTemplate trt;
	
	private static HashSet<String> singleTableList;
	private static HashSet<String> multiTableList;
	private static HashSet<String> badTableList;
	
	@BeforeClass
	public static void setUp() {
		singleTableList = new HashSet<String>();
		multiTableList = new HashSet<String>();
		badTableList = new HashSet<String>();
		singleTableList.add("customers");
		multiTableList.add("customers");
		multiTableList.add("PiCtURes");
		badTableList.add("customers");
		badTableList.add("badTable");
	} //end setUp
	
	@Test
	public void contextLoads() throws Exception {
		assertThat(readController).isNotNull();
	} //end contextLoads
	
	@Test
	public void validSingleTableRequestReturnsSuccess() throws Exception {
		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBAdmin");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tableList", singleTableList);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/readTables", parameters, String.class))
		.contains("\"name\":", "\"headers\":", "\"contents\":");
	} //end validSingleTableRequestReturnsSuccess
	
	@Test
	public void validMultiTableRequestReturnsSuccess() throws Exception {
		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBAdmin");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tableList", multiTableList);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/readTables", parameters, String.class))
		.contains("\"name\":", "\"headers\":", "\"contents\":", "}]},{");;
	} //end validMultiTableRequestReturnsSuccess
	
	@Test
	public void invalidURLReturnsFail() throws Exception {
		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		parameters.add("dburl", "badhost:1521/xe");
		parameters.add("dbuser", "DBAdmin");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tableList", singleTableList);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/readTables", parameters, String.class))
		.contains("failed");
	} //end invalidURLReturnsFail
	
	@Test
	public void invalidCredentialsReturnsFail() throws Exception {
		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "fakeuser");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tableList", singleTableList);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/readTables", parameters, String.class))
		.contains("Error");
	} //end invalidCredentialsReturnsFail
	
	@Test
	public void invalidTableListReturnsFail() throws Exception {
		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBAdmin");
		parameters.add("dbpass", "p4ssw0rd");
		parameters.add("tableList", badTableList);
		assertThat(this.trt.postForObject("http://localhost:" + port + "/readTables", parameters, String.class))
		.contains("not exist");
	} //end invalidTableListReturnsFail
} //end TableReadExtractTest
