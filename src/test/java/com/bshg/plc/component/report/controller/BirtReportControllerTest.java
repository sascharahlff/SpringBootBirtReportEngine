package com.bshg.plc.component.report.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.bshg.plc.component.report.Application;
import com.bshg.plc.component.report.service.ReportService;
import com.bshg.plc.component.report.utils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class, WebsocketSourceConfiguration.class, BirtReportController.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
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

		MockMultipartFile file = TestUtils.getMockMultipartFile(SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_1);

		mockMvc.perform(MockMvcRequestBuilders.multipart(location)
				.file(file))
				.andExpect(status().is(HttpStatus.CREATED.value())).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$", Matchers.hasSize(1)));
	}

	@Test
	public void uploadMultipleFilesToTempFolderTest() throws Exception {
		MvcResult result = mockMvc.perform(post("/report/component")).andExpect(status().is(HttpStatus.CREATED.value())).andReturn();
		String location = result.getResponse().getHeader("Location");

		MockMultipartFile file1 = TestUtils.getMockMultipartFile(SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_1);
		MockMultipartFile file2 = TestUtils.getMockMultipartFile(SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_2);
		MockMultipartFile file3 = TestUtils.getMockMultipartFile(SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_3);
		MockMultipartFile file4 = TestUtils.getMockMultipartFile(SAMPLE_ASSET_FOLDER + SAMPLE_XML);

		mockMvc.perform(MockMvcRequestBuilders.multipart(location)
				.file(file1).file(file2).file(file3).file(file4))
				.andExpect(status().is(HttpStatus.CREATED.value())).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$", Matchers.hasSize(4)))
				.andExpect(jsonPath("$.[0].origin", Matchers.is(SAMPLE_IMAGE_1)))
				.andExpect(jsonPath("$.[1].origin", Matchers.is(SAMPLE_IMAGE_2)))
				.andExpect(jsonPath("$.[2].origin", Matchers.is(SAMPLE_IMAGE_3)))
				.andExpect(jsonPath("$.[3].origin", Matchers.is(SAMPLE_XML)));
	}
}
