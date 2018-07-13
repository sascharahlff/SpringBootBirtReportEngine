package com.bshg.plc.component.report.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.After;
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
import com.bshg.plc.component.report.constants.Constants;
import com.bshg.plc.component.report.service.ReportService;
import com.bshg.plc.component.report.utils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class, WebsocketSourceConfiguration.class, BirtReportController.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BirtReportControllerTest {
	private static final String DATA_PATH = "/data";
	private static final String RESOURCES_PATH = "/resources";
	private static final String MULTIPART_REQUEST_PARAM_NAME = "files";
	private static final String XML_REQUEST_PARAM_NAME = "data";
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

	List<String> tempFolders = new ArrayList<String>();

	@Autowired
	ObjectMapper mapper;

	@After
	public void removeAllTemporaryFolders() {
		for (String uuid : tempFolders) {
			File folder = new File(Constants.REPORT_TEMP_UPLOAD_PATH + uuid);

			FileUtils.deleteQuietly(folder);
		}
	}

	@Test
	public void createUniqueFolderTest() throws Exception {
		MvcResult result = mockMvc.perform(post("/report/component")).andExpect(status().is(HttpStatus.CREATED.value())).andReturn();
		String location = result.getResponse().getHeader("Location");
		addFolderToDelete(location);
	}

	@Test
	public void uploadFileToTempFolderTest() throws Exception {
		MvcResult result = mockMvc.perform(post("/report/component")).andExpect(status().is(HttpStatus.CREATED.value())).andReturn();
		String location = result.getResponse().getHeader("Location");
		addFolderToDelete(location);

		MockMultipartFile file = TestUtils.getMockMultipartFile(MULTIPART_REQUEST_PARAM_NAME, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_1);

		mockMvc.perform(MockMvcRequestBuilders.multipart(location + "/resources").file(file))
				.andExpect(status().is(HttpStatus.CREATED.value())).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$", Matchers.hasSize(1)));
	}

	@Test
	public void uploadMultipleFilesToTempFolderTest() throws Exception {
		MvcResult result = mockMvc.perform(post("/report/component")).andExpect(status().is(HttpStatus.CREATED.value())).andReturn();
		String location = result.getResponse().getHeader("Location");
		addFolderToDelete(location);

		MockMultipartFile file1 = TestUtils.getMockMultipartFile(MULTIPART_REQUEST_PARAM_NAME, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_1);
		MockMultipartFile file2 = TestUtils.getMockMultipartFile(MULTIPART_REQUEST_PARAM_NAME, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_2);
		MockMultipartFile file3 = TestUtils.getMockMultipartFile(MULTIPART_REQUEST_PARAM_NAME, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_3);
		MockMultipartFile file4 = TestUtils.getMockMultipartFile(MULTIPART_REQUEST_PARAM_NAME, SAMPLE_ASSET_FOLDER + SAMPLE_XML);

		mockMvc.perform(MockMvcRequestBuilders.multipart(location + RESOURCES_PATH)
				.file(file1).file(file2).file(file3).file(file4))
				.andExpect(status().is(HttpStatus.CREATED.value())).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$", Matchers.hasSize(4)))
				.andExpect(jsonPath("$.[0].origin", Matchers.is(SAMPLE_IMAGE_1)))
				.andExpect(jsonPath("$.[1].origin", Matchers.is(SAMPLE_IMAGE_2)))
				.andExpect(jsonPath("$.[2].origin", Matchers.is(SAMPLE_IMAGE_3)))
				.andExpect(jsonPath("$.[3].origin", Matchers.is(SAMPLE_XML)));
	}

	@Test
	public void uploadXmlFile() throws Exception {
		MvcResult result = mockMvc.perform(post("/report/component")).andExpect(status().is(HttpStatus.CREATED.value())).andReturn();
		String location = result.getResponse().getHeader("Location");
		addFolderToDelete(location);

		MockMultipartFile file = TestUtils.getMockMultipartFile(XML_REQUEST_PARAM_NAME, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_1);
		
		System.out.println(location + DATA_PATH);
		mockMvc.perform(MockMvcRequestBuilders.multipart(location + "/data")
				.file(file))
				.andExpect(status().is(HttpStatus.NO_CONTENT.value()));
	}

	@Test
	public void removeTemporaryFolder() throws Exception {
		MvcResult result = mockMvc.perform(post("/report/component")).andExpect(status().is(HttpStatus.CREATED.value())).andReturn();
		String location = result.getResponse().getHeader("Location");
		String uuid = getUUIDFromLocation(location);

		mockMvc.perform(get("/report/component/" + uuid)).andExpect(status().is(HttpStatus.NO_CONTENT.value()));
	}

	// Helper-Function to delete all created folders
	private void addFolderToDelete(final String location) {
		String uuid = getUUIDFromLocation(location);

		if (!uuid.isEmpty()) {
			tempFolders.add(uuid);
		}
	}

	// Helper-Function to extract uuid from location
	private String getUUIDFromLocation(final String location) {
		String uuid = "";

		if (!location.isEmpty()) {
			String[] elements = location.split("/");

			if (elements.length > 1) {
				uuid = elements[elements.length - 1];

				if (uuid.length() != 36) {
					uuid = "";
				}
			}
		}

		return uuid;
	}
}
