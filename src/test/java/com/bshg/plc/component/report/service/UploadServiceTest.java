package com.bshg.plc.component.report.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class })
@AutoConfigureMockMvc
public class UploadServiceTest {
	private static final String SAMPLE_ASSET_FOLDER = "classpath:assets/";
	private static final String SAMPLE_IMAGE_FOLDER = "classpath:images/";
	private static final String SAMPLE_IMAGE_1 = "hasi.png";
	private static final String SAMPLE_IMAGE_2 = "cozmo1.png";
	private static final String SAMPLE_IMAGE_3 = "cozmo2.png";
	private static final String SAMPLE_XML = "bsh.xml";

	@Autowired
	UploadService uploadService;

	@Test
	public void createUniqueFolderTest() throws FileNotFoundException {
		String folderName = uploadService.createUniqueFolder();

		assertNotNull(folderName);

		File folder = new File(Constants.REPORT_TEMP_UPLOAD_PATH + folderName);
		assertEquals(true, folder.exists());
	}

	@Test
	public void uploadFileToTempFolderTest() throws Exception {
		String folderName = uploadService.createUniqueFolder();
		List<MultipartFile> files = new ArrayList<MultipartFile>();

		File file = ResourceUtils.getFile(SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_1);
		assertEquals(true, file.exists());

		MockMultipartFile mFile = new MockMultipartFile("files", file.getName(), "image/png", loadFileAsBytesArray(file));
		files.add(mFile);

		List<ReportAsset> fileList = uploadService.uploadFiles(files, folderName);
		assertEquals(1, fileList.size());
	}

	@Test
	public void uploadMultipleFilesToTempFolderTest() throws Exception {
		String folderName = uploadService.createUniqueFolder();
		List<MultipartFile> files = new ArrayList<MultipartFile>();

		MockMultipartFile file1 = getMultipartFile(SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_1);
		MockMultipartFile file2 = getMultipartFile(SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_2);
		MockMultipartFile file3 = getMultipartFile(SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_3);
		MockMultipartFile file4 = getMultipartFile(SAMPLE_ASSET_FOLDER + SAMPLE_XML);
		
		files.addAll(Arrays.asList(file1, file2, file3, file4));

		List<ReportAsset> fileList = uploadService.uploadFiles(files, folderName);
		assertEquals(4, fileList.size());
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
