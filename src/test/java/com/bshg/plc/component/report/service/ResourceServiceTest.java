package com.bshg.plc.component.report.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
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
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bshg.plc.component.report.Application;
import com.bshg.plc.component.report.constants.Constants;
import com.bshg.plc.component.report.domain.ReportAsset;
import com.bshg.plc.component.report.utils.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class ResourceServiceTest {
	private static final String SAMPLE_ASSET_FOLDER = "classpath:assets/";
	private static final String SAMPLE_IMAGE_FOLDER = "classpath:images/";
	private static final String SAMPLE_IMAGE_1 = "hasi.png";
	private static final String SAMPLE_IMAGE_2 = "cozmo1.png";
	private static final String SAMPLE_IMAGE_3 = "cozmo2.png";
	private static final String SAMPLE_XML = "bsh.xml";

	List<String> tempFolders = new ArrayList<String>();
	
	@Autowired
	ResourceService resourceService;
	
	@After
	public void removeAllTemporaryFolders() {
		// Remove all test folders and files
		for (String folder : tempFolders) {
			resourceService.removeTemporaryFolder(folder);
		}
	}
	
	@Test
	public void createTemporaryFolder() throws FileNotFoundException {
		// Create temporary folder
		String folderName = resourceService.createTempFolder();
		tempFolders.add(folderName);

		assertNotNull(folderName);

		// Check if folder exists
		File folder = new File(Constants.REPORT_TEMP_UPLOAD_PATH + folderName);
		assertEquals(true, folder.exists());
	}

	@Test
	public void uploadFileToTempFolder() throws Exception {
		List<MultipartFile> files = new ArrayList<MultipartFile>();

		// Create temporary folder
		String folderName = resourceService.createTempFolder();
		tempFolders.add(folderName);

		// Create a multipart file to upload
		File file = ResourceUtils.getFile(SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_1);
		assertEquals(true, file.exists());

		// Upload a file to temporary folder
		MockMultipartFile mFile = new MockMultipartFile(Constants.REQUEST_PARAM_MULTIPART, file.getName(), "image/png", TestUtils.readFileAsBytesArray(file));
		files.add(mFile);
		
		// Check if file exists
		List<ReportAsset> fileList = resourceService.uploadMultipartFiles(folderName, files);
		assertEquals(1, fileList.size());
	}

	@Test
	public void uploadMultipleFilesToTempFolder() throws Exception {
		List<MultipartFile> files = new ArrayList<MultipartFile>();

		// Create temporary folder
		String folderName = resourceService.createTempFolder();
		tempFolders.add(folderName);
		
		// Create multipart files to upload
		MockMultipartFile file1 = TestUtils.getMockMultipartFile(Constants.REQUEST_PARAM_MULTIPART, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_1);
		MockMultipartFile file2 = TestUtils.getMockMultipartFile(Constants.REQUEST_PARAM_MULTIPART, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_2);
		MockMultipartFile file3 = TestUtils.getMockMultipartFile(Constants.REQUEST_PARAM_MULTIPART, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_3);
		MockMultipartFile file4 = TestUtils.getMockMultipartFile(Constants.REQUEST_PARAM_MULTIPART, SAMPLE_ASSET_FOLDER + SAMPLE_XML);
		files.addAll(Arrays.asList(file1, file2, file3, file4));
	
		// Check if files exists
		List<ReportAsset> fileList = resourceService.uploadMultipartFiles(folderName, files);
		assertEquals(4, fileList.size());
		assertEquals(((ReportAsset) fileList.get(0)).getOrigin(), SAMPLE_IMAGE_1);
		assertEquals(((ReportAsset) fileList.get(1)).getOrigin(), SAMPLE_IMAGE_2);
		assertEquals(((ReportAsset) fileList.get(2)).getOrigin(), SAMPLE_IMAGE_3);
		assertEquals(((ReportAsset) fileList.get(3)).getOrigin(), SAMPLE_XML);
	}
	
	@Test
	public void uploadXmlFile() throws Exception {
		// Create temporary folder
		String folderName = resourceService.createTempFolder();
		tempFolders.add(folderName);
		
		// Create and upload XML file to temporary folder
		MockMultipartFile file = TestUtils.getMockMultipartFile(Constants.REQUEST_PARAM_XML, SAMPLE_ASSET_FOLDER + SAMPLE_XML);
		assertEquals(true, resourceService.uploadDataXml(folderName, file));

		// Check if file exists
		File xmlData = new File(Constants.REPORT_TEMP_UPLOAD_PATH + folderName +"/data.xml");
		assertEquals(true, xmlData.exists());
	}
	
	@Test
	public void removeTemporaryFolder() throws FileNotFoundException {
		// Create temporary folder
		String folderName = resourceService.createTempFolder();
		
		// Check if temporary folder is removed
		assertEquals(true, resourceService.removeTemporaryFolder(folderName));
	}
}
