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
@SpringBootTest(classes = { Application.class })
@AutoConfigureMockMvc
public class UploadServiceTest {
	private static final String MULTIPART_REQUEST_PARAM_NAME = "files";
	private static final String XML_REQUEST_PARAM_NAME = "data";
	private static final String SAMPLE_ASSET_FOLDER = "classpath:assets/";
	private static final String SAMPLE_IMAGE_FOLDER = "classpath:images/";
	private static final String SAMPLE_IMAGE_1 = "hasi.png";
	private static final String SAMPLE_IMAGE_2 = "cozmo1.png";
	private static final String SAMPLE_IMAGE_3 = "cozmo2.png";
	private static final String SAMPLE_XML = "bsh.xml";

	List<String> tempFolders = new ArrayList<String>();
	
	@Autowired
	UploadService uploadService;
	
	@After
	public void removeAllTemporaryFolders() {
		for (String folder : tempFolders) {
			uploadService.removeTemporaryFolder(folder);
		}
	}
	
	@Test
	public void createTemporaryFolderTest() throws FileNotFoundException {
		String folderName = uploadService.createTempFolder();
		tempFolders.add(folderName);

		assertNotNull(folderName);

		File folder = new File(Constants.REPORT_TEMP_UPLOAD_PATH + folderName);
		assertEquals(true, folder.exists());
	}

	@Test
	public void uploadFileToTempFolderTest() throws Exception {
		String folderName = uploadService.createTempFolder();
		List<MultipartFile> files = new ArrayList<MultipartFile>();
		tempFolders.add(folderName);

		File file = ResourceUtils.getFile(SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_1);
		assertEquals(true, file.exists());

		MockMultipartFile mFile = new MockMultipartFile("files", file.getName(), "image/png", TestUtils.loadFileAsBytesArray(file));
		files.add(mFile);

		List<ReportAsset> fileList = uploadService.uploadMultipartFiles(files, folderName);
		assertEquals(1, fileList.size());
	}

	@Test
	public void uploadMultipleFilesToTempFolderTest() throws Exception {
		String folderName = uploadService.createTempFolder();
		List<MultipartFile> files = new ArrayList<MultipartFile>();
		tempFolders.add(folderName);

		MockMultipartFile file1 = TestUtils.getMockMultipartFile(MULTIPART_REQUEST_PARAM_NAME, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_1);
		MockMultipartFile file2 = TestUtils.getMockMultipartFile(MULTIPART_REQUEST_PARAM_NAME, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_2);
		MockMultipartFile file3 = TestUtils.getMockMultipartFile(MULTIPART_REQUEST_PARAM_NAME, SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_3);
		MockMultipartFile file4 = TestUtils.getMockMultipartFile(MULTIPART_REQUEST_PARAM_NAME, SAMPLE_ASSET_FOLDER + SAMPLE_XML);
		
		files.addAll(Arrays.asList(file1, file2, file3, file4));

		List<ReportAsset> fileList = uploadService.uploadMultipartFiles(files, folderName);
		assertEquals(4, fileList.size());
		assertEquals(((ReportAsset) fileList.get(0)).getOrigin(), SAMPLE_IMAGE_1);
		assertEquals(((ReportAsset) fileList.get(1)).getOrigin(), SAMPLE_IMAGE_2);
		assertEquals(((ReportAsset) fileList.get(2)).getOrigin(), SAMPLE_IMAGE_3);
		assertEquals(((ReportAsset) fileList.get(3)).getOrigin(), SAMPLE_XML);
	}
	
	@Test
	public void uploadXmlFile() throws Exception {
		String folderName = uploadService.createTempFolder();
		tempFolders.add(folderName);
		MockMultipartFile file = TestUtils.getMockMultipartFile(XML_REQUEST_PARAM_NAME, SAMPLE_ASSET_FOLDER + SAMPLE_XML);
		
		assertEquals(true, uploadService.uploadDataXml(file, folderName));
		
		File xmlData = new File(Constants.REPORT_TEMP_UPLOAD_PATH + folderName +"/data.xml");
		assertEquals(true, xmlData.exists());
	}
	
	@Test
	public void removeTemporaryFolder() throws FileNotFoundException {
		String folderName = uploadService.createTempFolder();
		assertEquals(true, uploadService.removeTemporaryFolder(folderName));
	}
}
