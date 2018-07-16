package com.bshg.plc.component.report.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tomcat.jni.File;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.bshg.plc.component.report.Application;
import com.bshg.plc.component.report.constants.Constants;
import com.bshg.plc.component.report.domain.ReportAsset;
import com.bshg.plc.component.report.utils.ReportUtils;
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
	// private static final String SAMPLE_XML = "bsh.xml";

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
		List<MultipartFile> files = new ArrayList<MultipartFile>();

		// Create temporary folder
		String folderName = resourceService.createTempFolder();
		tempFolders.add(folderName);

		// Image upload
		MockMultipartFile file1 = TestUtils.getMockMultipartFile(MULTIPART_REQUEST_PARAM_NAME,
				SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_1);
		MockMultipartFile file2 = TestUtils.getMockMultipartFile(MULTIPART_REQUEST_PARAM_NAME,
				SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_2);
		MockMultipartFile file3 = TestUtils.getMockMultipartFile(MULTIPART_REQUEST_PARAM_NAME,
				SAMPLE_IMAGE_FOLDER + SAMPLE_IMAGE_3);
		files.addAll(Arrays.asList(file1, file2, file3));
		List<ReportAsset> fileList = resourceService.uploadMultipartFiles(folderName, files);

		// Create XML
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
		
		try {
			java.io.FileWriter fw = new java.io.FileWriter(Constants.REPORT_TEMP_UPLOAD_PATH + folderName + "/data.xml");
			fw.write(xml.toString());
			fw.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		
		
		
		
		
		

//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder builder;
//		
//		try {
//			builder = factory.newDocumentBuilder();
//			Document document = builder.parse(new InputSource(new StringReader(xml.toString())));
//			System.out.println(new XMLDocument(document).toString(););
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		
//		// XML upload
//		MockMultipartFile file = TestUtils.getMockMultipartFile(XML_REQUEST_PARAM_NAME, SAMPLE_ASSET_FOLDER + SAMPLE_XML);
//		resourceService.uploadDataXml(folderName, file);
//
//		// Prefill report
//		byte[] bytes = reportService.createReport("d6e17719-63da-4720-ab4e-ad2f2557de24");
//		System.out.println(bytes);
	}
}
