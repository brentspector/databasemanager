package com.brent.databasemanager.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

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

import com.brent.databasemanager.controllers.FileUpload;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UploadTableTest {

	@LocalServerPort
	private int port;
	
	@Autowired
	private FileUpload uploadController;
	
	@Autowired
	private TestRestTemplate trt;
	
	@Test
	public void contextLoads() throws Exception {
		assertThat(uploadController).isNotNull();
	}
	
	@Test
	public void validCSVFileReturnsSuccess() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("file", new ClassPathResource("static/ur-collection1.csv"));
		parameters.add("type", "AUTO");
		assertThat(this.trt.postForObject("http://localhost:" + port + "/uploadNewTable", parameters, String.class))
		.contains("\"name\":\"\"", "\"headers\":", "\"contents\":");
	}
	
	@Test
	public void validTXTAsINIFileReturnsSuccess() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("file", new ClassPathResource("static/AmbiguousINI.txt"));
		parameters.add("type", "INI");
		assertThat(this.trt.postForObject("http://localhost:" + port + "/uploadNewTable", parameters, String.class))
		.contains("\"name\":\"\"", "\"headers\":", "\"contents\":");
	}
	
	@Test
	public void validTXTAsCSVFileReturnsSuccess() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("file", new ClassPathResource("static/AmbiguousCSV.txt"));
		parameters.add("type", "CSV");
		assertThat(this.trt.postForObject("http://localhost:" + port + "/uploadNewTable", parameters, String.class))
		.contains("\"name\":\"\"", "\"headers\":", "\"contents\":");
	}
	
	@Test
	public void validINIFileReturnsSuccess() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("file", new ClassPathResource("static/ValidINI.ini"));
		parameters.add("type", "AUTO");
		assertThat(this.trt.postForObject("http://localhost:" + port + "/uploadNewTable", parameters, String.class))
		.contains("\"name\":\"\"", "\"headers\":", "\"contents\":");
	}
	
	@Test
	public void badExtensionReturnsBadExtensionError() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("file", new ClassPathResource("static/BogusTest.txt"));
		parameters.add("type", "AUTO");
		assertThat(this.trt.postForObject("http://localhost:" + port + "/uploadNewTable", parameters, String.class))
		.contains("{\"status\":\"BAD_REQUEST\",\"userMessage\":\"To upload TXT documents, please specify the format type\",\"debugMessage\":\"To upload TXT documents, please specify the format type\",\"subErrors\":null}");
	}
	
	@Test
	public void badCSVFormatReturnsBadFormatError() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("file", new ClassPathResource("static/BogusTest.txt"));
		parameters.add("type", "CSV");
		assertThat(this.trt.postForObject("http://localhost:" + port + "/uploadNewTable", parameters, String.class))
		.contains("{\"status\":\"BAD_REQUEST\",\"userMessage\":\"Bad Format - Each record must have new line\",\"debugMessage\":null,\"subErrors\":null}");
	}
	
	@Test
	public void badINIFormatReturnsBadFormatError() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("file", new ClassPathResource("static/BogusTest.txt"));
		parameters.add("type", "INI");
		assertThat(this.trt.postForObject("http://localhost:" + port + "/uploadNewTable", parameters, String.class))
		.contains("{\"status\":\"BAD_REQUEST\",\"userMessage\":\"Bad Format - Missing Bracket(s) for the following: Invalid Text for Upload Tests\",\"debugMessage\":null,\"subErrors\":null}");
	}
	
	@Test
	public void testTXTAsCSVFileReturnsSuccess() throws Exception {
		MultiValueMap<String,Object> parameters = new LinkedMultiValueMap<String,Object>();
		parameters.add("file", new ClassPathResource("static/DecimalDateDTime.txt"));
		parameters.add("type", "CSV");
		assertThat(this.trt.postForObject("http://localhost:" + port + "/uploadNewTable", parameters, String.class))
		.contains("\"name\":\"\"", "\"headers\":", "\"contents\":");
	}
}
