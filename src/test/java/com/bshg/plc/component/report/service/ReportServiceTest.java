package com.bshg.plc.component.report.service;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import com.bshg.plc.component.report.Application;
import com.bshg.plc.component.report.domain.ReportAsset;
import com.bshg.plc.component.report.utils.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class ReportServiceTest {
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
	ResourceService resourceService;

	List<String> tempFolders = new ArrayList<String>();

	@After
	public void removeAllTemporaryFolders() {
		for (String folder : tempFolders) {
			// resourceService.removeTemporaryFolder(folder);
		}
	}

	@Test
	public void createReport() throws Exception {
//		List<MultipartFile> files = new ArrayList<MultipartFile>();
//
//		// Create temporary folder
//		String folderName = resourceService.createTempFolder();
//		tempFolders.add(folderName);
//
//		// Image upload
//		MockMultipartFile file1 = TestUtils.getMockMultipartFile(MULTIPART_REQUEST_PARAM_NAME, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_1);
//		MockMultipartFile file2 = TestUtils.getMockMultipartFile(MULTIPART_REQUEST_PARAM_NAME, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_2);
//		MockMultipartFile file3 = TestUtils.getMockMultipartFile(MULTIPART_REQUEST_PARAM_NAME, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_3);
//		files.addAll(Arrays.asList(file1, file2, file3));
//		List<ReportAsset> fileList = resourceService.uploadMultipartFiles(files, folderName);
//
//		// XML upload
//		MockMultipartFile file = TestUtils.getMockMultipartFile(XML_REQUEST_PARAM_NAME, SAMPLE_ASSET_FOLDER + SAMPLE_XML);
//		resourceService.uploadDataXml(file, folderName);

		// Prefill report
		byte[] bytes = reportService.createReport("d6e17719-63da-4720-ab4e-ad2f2557de24");
		System.out.println(bytes);
	}
}
