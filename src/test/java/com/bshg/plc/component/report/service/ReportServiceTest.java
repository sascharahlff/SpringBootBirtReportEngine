package com.bshg.plc.component.report.service;

import static org.junit.Assert.assertNotEquals;

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
import com.bshg.plc.component.report.constants.Constants;
import com.bshg.plc.component.report.domain.ReportAsset;
import com.bshg.plc.component.report.utils.ReportUtils;
import com.bshg.plc.component.report.utils.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class ReportServiceTest {
	private static final String SAMPLE_IMAGE_FOLDER = "classpath:images/";
	private static final String SAMPLE_IMAGE_1 = "hasi.png";
	private static final String SAMPLE_IMAGE_2 = "cozmo1.png";
	private static final String SAMPLE_IMAGE_3 = "cozmo2.png";

	@Autowired
	ReportService reportService;

	@Autowired
	ResourceService resourceService;

	List<String> tempFolders = new ArrayList<String>();

	@After
	public void removeAllTemporaryFolders() {
		for (String folder : tempFolders) {
			 resourceService.removeTemporaryFolder(folder);
		}
	}

	@Test
	public void createReport() throws Exception {
		List<MultipartFile> files = new ArrayList<MultipartFile>();

		// Create temporary folder
		String folderName = resourceService.createTempFolder();
		tempFolders.add(folderName);

		// Upload images to temporary folder
		MockMultipartFile file1 = TestUtils.getMockMultipartFile(Constants.REQUEST_PARAM_MULTIPART, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_1);
		MockMultipartFile file2 = TestUtils.getMockMultipartFile(Constants.REQUEST_PARAM_MULTIPART, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_2);
		MockMultipartFile file3 = TestUtils.getMockMultipartFile(Constants.REQUEST_PARAM_MULTIPART, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_3);
		files.addAll(Arrays.asList(file1, file2, file3));
		List<ReportAsset> fileList = resourceService.uploadMultipartFiles(folderName, files);

		// Create data.xml
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
		java.io.FileWriter fileWriter = new java.io.FileWriter(Constants.REPORT_TEMP_UPLOAD_PATH + folderName +"/"+ Constants.XML_FILE_NAME);
		fileWriter.write(xml.toString());
		fileWriter.close();
		
		// Create Report
		byte[] byteArray = reportService.createReport(folderName);
		assertNotEquals(0, byteArray.length);
	}
}
