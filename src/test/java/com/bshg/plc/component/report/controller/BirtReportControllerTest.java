package com.bshg.plc.component.report.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.FilenameUtils;
import org.hamcrest.Matchers;
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
import com.bshg.plc.component.report.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class, WebsocketSourceConfiguration.class,
		BirtReportController.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BirtReportControllerTest {
	private static final String SAMPLE_ASSET_FOLDER = "classpath:assets/";
	private static final String SAMPLE_IMAGE_FOLDER = "classpath:images/";
	private static final String SAMPLE_IMAGE_1 = "hasi.png";
	private static final String SAMPLE_IMAGE_2 = "cozmo1.png";
	private static final String SAMPLE_IMAGE_3 = "cozmo2.png";
	private static final String SAMPLE_XML = "bsh.xml";

	@Autowired
	ReportService reportService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper mapper;

	@Test
	public void createUniqueFolderTest() throws Exception {
		mockMvc.perform(post("/report/component")).andExpect(status().is(HttpStatus.CREATED.value()));
	}

	@Test
	public void uploadFileToTempFolderTest() throws Exception {
		MvcResult result = mockMvc.perform(post("/report/component")).andExpect(status().is(HttpStatus.CREATED.value())).andReturn();
		String location = result.getResponse().getHeader("Location");

		MockMultipartFile file = getMultipartFile(SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_1);

		mockMvc.perform(MockMvcRequestBuilders.multipart(location)
				.file(file))
				.andExpect(status().is(HttpStatus.CREATED.value())).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$", Matchers.hasSize(1)));
	}

	@Test
	public void uploadMultipleFilesToTempFolderTest() throws Exception {
		MvcResult result = mockMvc.perform(post("/report/component")).andExpect(status().is(HttpStatus.CREATED.value())).andReturn();
		String location = result.getResponse().getHeader("Location");

		MockMultipartFile file1 = getMultipartFile(SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_1);
		MockMultipartFile file2 = getMultipartFile(SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_2);
		MockMultipartFile file3 = getMultipartFile(SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_3);
		MockMultipartFile file4 = getMultipartFile(SAMPLE_ASSET_FOLDER + SAMPLE_XML);

		mockMvc.perform(MockMvcRequestBuilders.multipart(location)
				.file(file1).file(file2).file(file3).file(file4))
				.andExpect(status().is(HttpStatus.CREATED.value())).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$", Matchers.hasSize(4)));
	}

	private MockMultipartFile getMultipartFile(String assetName) throws Exception {
		File file = ResourceUtils.getFile(assetName);
		String fileExtension = FilenameUtils.getExtension(assetName);
		String contentType = "image/png";

		if (fileExtension.toLowerCase() == "xml") {
			contentType = "text/xml";
		}

		return new MockMultipartFile("files", file.getName(), contentType, loadFileAsBytesArray(file));
	}

	private byte[] loadFileAsBytesArray(File file) throws Exception {
		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
		int length = (int) file.length();
		byte[] bytes = new byte[length];
		reader.read(bytes, 0, length);
		reader.close();

		return bytes;
	}
}
