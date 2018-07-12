package com.bshg.plc.component.report.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import org.hamcrest.core.StringContains;

import com.bshg.plc.component.report.Application;
import com.bshg.plc.component.report.constants.Constants;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class })
@AutoConfigureMockMvc
public class UploadServiceTest {
	private static final String SAMPLE_IMAGE1_PATH = "classpath:assets/images/hasi.png";
//	private static final String SAMPLE_IMAGE2_PATH = "classpath:assets/images/cozmo1.png";
//	private static final String SAMPLE_IMAGE3_PATH = "classpath:assets/images/cozmo2.png";

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
	public void uploadFileTest() throws Exception {
		String folderName = uploadService.createUniqueFolder();
		List<MultipartFile> files = new ArrayList<MultipartFile>();

		File file = ResourceUtils.getFile(SAMPLE_IMAGE1_PATH);
		assertEquals(true, file.exists());

		MockMultipartFile mFile = new MockMultipartFile("files", file.getName(), "image/png", loadFileAsBytesArray(file));
		files.add(mFile);

		Map<String, String> fileList = uploadService.uploadFiles(files, folderName);
		assertEquals(true, fileList.get("hasi.png").contains(".png"));
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
