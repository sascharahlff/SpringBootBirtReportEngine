package com.bshg.plc.component.report.controller;

import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileWriter;
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
import com.bshg.plc.component.report.domain.ReportAsset;
import com.bshg.plc.component.report.service.ReportService;
import com.bshg.plc.component.report.utils.ReportUtils;
import com.bshg.plc.component.report.utils.TestUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class, WebsocketSourceConfiguration.class, ReportController.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ReportControllerTest {
	private static final String RESOURCES_PATH = "/resources";
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
		// Remove all test folders and files
		for (String uuid : tempFolders) {
			File folder = new File(Constants.REPORT_TEMP_UPLOAD_PATH + uuid);

			FileUtils.deleteQuietly(folder);
		}
	}

	@Test
	public void createTemporaryFolder() throws Exception {
		// Create temporary folder
		MvcResult result = mockMvc.perform(post("/report/component")).andExpect(status().is(HttpStatus.CREATED.value())).andReturn();
		String location = result.getResponse().getHeader("Location");
		addFolderToDelete(location);
	}

	@Test
	public void uploadFileToTempFolder() throws Exception {
		// Create temporary folder
		MvcResult result = mockMvc.perform(post("/report/component")).andExpect(status().is(HttpStatus.CREATED.value())).andReturn();
		String location = result.getResponse().getHeader("Location");
		addFolderToDelete(location);

		// Create a multipart file to upload
		MockMultipartFile file = TestUtils.getMockMultipartFile(Constants.REQUEST_PARAM_MULTIPART, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_1);

		// Upload a file to temporary folder
		mockMvc.perform(MockMvcRequestBuilders.multipart(location + "/resources").file(file))
				.andExpect(status().is(HttpStatus.CREATED.value())).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$", Matchers.hasSize(1)));
	}

	@Test
	public void uploadMultipleFilesToTempFolder() throws Exception {
		// Create temporary folder
		MvcResult result = mockMvc.perform(post("/report/component")).andExpect(status().is(HttpStatus.CREATED.value())).andReturn();
		String location = result.getResponse().getHeader("Location");
		addFolderToDelete(location);

		// Create multipart files to upload
		MockMultipartFile file1 = TestUtils.getMockMultipartFile(Constants.REQUEST_PARAM_MULTIPART, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_1);
		MockMultipartFile file2 = TestUtils.getMockMultipartFile(Constants.REQUEST_PARAM_MULTIPART, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_2);
		MockMultipartFile file3 = TestUtils.getMockMultipartFile(Constants.REQUEST_PARAM_MULTIPART, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_3);
		MockMultipartFile file4 = TestUtils.getMockMultipartFile(Constants.REQUEST_PARAM_MULTIPART, SAMPLE_ASSET_FOLDER + SAMPLE_XML);

		// Upload files to temporary folder
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
		// Create temporary folder
		MvcResult result = mockMvc.perform(post("/report/component")).andExpect(status().is(HttpStatus.CREATED.value())).andReturn();
		String location = result.getResponse().getHeader("Location");
		addFolderToDelete(location);

		// Create XML file to upload
		MockMultipartFile file = TestUtils.getMockMultipartFile(Constants.REQUEST_PARAM_XML, SAMPLE_ASSET_FOLDER + SAMPLE_XML);
		
		// Upload XML file to temporary folder
		mockMvc.perform(MockMvcRequestBuilders.multipart(location + "/data")
				.file(file))
				.andExpect(status().is(HttpStatus.NO_CONTENT.value()));
	}

	@Test
	public void createReport() throws Exception {
		// Create temporary folder
		MvcResult result = mockMvc.perform(post("/report/component")).andExpect(status().is(HttpStatus.CREATED.value())).andReturn();
		String location = result.getResponse().getHeader("Location");
		String folderName = getUUIDFromLocation(location);
		addFolderToDelete(location);

		// Create multipart files to upload
		MockMultipartFile file1 = TestUtils.getMockMultipartFile(Constants.REQUEST_PARAM_MULTIPART, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_1);
		MockMultipartFile file2 = TestUtils.getMockMultipartFile(Constants.REQUEST_PARAM_MULTIPART, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_2);
		MockMultipartFile file3 = TestUtils.getMockMultipartFile(Constants.REQUEST_PARAM_MULTIPART, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_3);
		
		// Upload files to temporary folder
		result = mockMvc.perform(MockMvcRequestBuilders.multipart(location + RESOURCES_PATH).file(file1).file(file2).file(file3)).andReturn();
		List<ReportAsset> fileList = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<ReportAsset>>() {});

		// Create XML data source
		StringBuilder xml = new StringBuilder();
		xml.append("<?xml version=\"1.0\"?><data>");
		xml.append("<name>Biff Tannen</name>");
		String reportPath = Constants.REPORT_TEMP_UPLOAD_PATH.replace("./", "");
		int row = 1;

		for (ReportAsset asset : fileList) {
			String extension = ReportUtils.getFileExtension(asset.getOrigin());
			String assetName = asset.getUuid() + "." + extension;

			if (row == 1) {
				xml.append("<firstImage>" + reportPath + folderName + "/" + assetName + "</firstImage>");
			} else if (row == 2) {
				xml.append("<secondImage>" + reportPath + folderName + "/" + assetName + "</secondImage>");
			} else if (row == 3) {
				xml.append("<thirdImage>" + reportPath + folderName + "/" + assetName + "</thirdImage>");
			}

			row++;
		}

		xml.append("</data>");
		
		// Write data.xml to temporary folder
		FileWriter fileWriter = new FileWriter(Constants.REPORT_TEMP_UPLOAD_PATH + folderName +"/"+ Constants.XML_FILE_NAME);
		fileWriter.write(xml.toString());
		fileWriter.close();

		// Create report and get PDF as byte array
		result = mockMvc.perform(get("/report/component/"+ folderName)).andExpect(status().is(HttpStatus.OK.value())).andReturn();
		byte[] byteArray = result.getResponse().getContentAsByteArray();
		assertNotEquals(0, byteArray.length);
	}
	
	@Test
	public void removeTemporaryFolder() throws Exception {
		// Create temporary folder
		MvcResult result = mockMvc.perform(post("/report/component")).andExpect(status().is(HttpStatus.CREATED.value())).andReturn();
		String location = result.getResponse().getHeader("Location");
		String uuid = getUUIDFromLocation(location);

		// Remove temporary folder
		mockMvc.perform(delete("/report/component/" + uuid)).andExpect(status().is(HttpStatus.NO_CONTENT.value()));
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
				// UUID is last element in location
				uuid = elements[elements.length - 1];

				if (uuid.length() != 36) {
					uuid = "";
				}
			}
		}

		return uuid;
	}
}
