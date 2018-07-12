package com.bshg.plc.component.report.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;

import com.bshg.plc.component.report.Application;
import com.bshg.plc.component.report.controller.BirtReportController;
import com.bshg.plc.component.report.service.ReportService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class, WebsocketSourceConfiguration.class, BirtReportController.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BirtReportTest {
	private static final String SAMPLE_IMAGE1_PATH = "classpath:assets/images/hasi.png";
	private static final String SAMPLE_IMAGE2_PATH = "classpath:assets/images/cozmo1.png";
	private static final String SAMPLE_IMAGE3_PATH = "classpath:assets/images/cozmo2.png";
	
	// private String sampleXml = "<?xml version=\"1.0\"
	// encoding=\"UTF-8\"?><library><name>Biff Tannen</name></library>";

	@Autowired
	ReportService reportService;

	@Autowired
	private MockMvc mockMvc;

	// @Test
	public void createTempFolder() throws Exception {
		mockMvc.perform(post("/report/component")).andExpect(status().is(HttpStatus.CREATED.value()));
	}

	@Test
	public void uploadFileToTempFolder() throws Exception {
		MvcResult result = mockMvc.perform(post("/report/component")).andExpect(status().is(HttpStatus.CREATED.value())).andReturn();
		String location = result.getResponse().getHeader("Location");

		File file1 = ResourceUtils.getFile(SAMPLE_IMAGE1_PATH);
		assertEquals(true, file1.exists());
		
		File file2 = ResourceUtils.getFile(SAMPLE_IMAGE2_PATH);
		assertEquals(true, file2.exists());

		File file3 = ResourceUtils.getFile(SAMPLE_IMAGE3_PATH);
		assertEquals(true, file3.exists());

		MockMultipartFile mFile1 = new MockMultipartFile("files", file1.getName(), "image/png", loadFileAsBytesArray(file1));
		MockMultipartFile mFile2 = new MockMultipartFile("files", file2.getName(), "image/png", loadFileAsBytesArray(file2));
		MockMultipartFile mFile3 = new MockMultipartFile("files", file3.getName(), "image/png", loadFileAsBytesArray(file3));

		mockMvc.perform(MockMvcRequestBuilders.multipart(location).file(mFile1).file(mFile2).file(mFile3)).andExpect(status().is(HttpStatus.CREATED.value()));
	}

	public static byte[] loadFileAsBytesArray(File file) throws Exception {
		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
		int length = (int) file.length();
		byte[] bytes = new byte[length];
		reader.read(bytes, 0, length);
		reader.close();

		return bytes;
	}
}
