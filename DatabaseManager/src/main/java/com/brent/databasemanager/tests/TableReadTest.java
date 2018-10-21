package com.brent.databasemanager.tests;

import static org.assertj.core.api.Assertions.assertThat;

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
public class TableReadTest {
	@LocalServerPort
	private int port;
	
	@Autowired
	TableRead readController;
	
	@Autowired
	TestRestTemplate trt;
	
	@Test
	public void contextLoads() throws Exception {
		assertThat(readController).isNotNull();
	} //end contextLoads
	
	@Test
	public void validConnectionDetailsReturnsSuccess() throws Exception {
		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "DBAdmin");
		parameters.add("dbpass", "p4ssw0rd");
		assertThat(this.trt.postForObject("http://localhost:" + port + "/read", parameters, String.class))
		.contains("CUSTOMERS");
	} //end validConnectionDetailsReturnsSuccess
	
	@Test
	public void invalidCredentialsReturnsFail() throws Exception {
		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		parameters.add("dburl", "localhost:1521/xe");
		parameters.add("dbuser", "fakeUser");
		parameters.add("dbpass", "p4ssw0rd");
		assertThat(this.trt.postForObject("http://localhost:" + port + "/read", parameters, String.class))
		.contains("Error");
	} //end invalidCredentialsReturnsFail
	
	@Test
	public void invalidURLReturnsFail() throws Exception {
		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		parameters.add("dburl", "notreal:1521/xe");
		parameters.add("dbuser", "DBAdmin");
		parameters.add("dbpass", "p4ssw0rd");
		assertThat(this.trt.postForObject("http://localhost:" + port + "/read", parameters, String.class))
		.contains("failed");
	} //end invalidURLReturnsFail
} //end TableReadTest
