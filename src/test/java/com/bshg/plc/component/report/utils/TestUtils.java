package com.bshg.plc.component.report.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.FilenameUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;

public class TestUtils {
	public static MockMultipartFile getMockMultipartFile(final String requestParamName, final String assetName) throws Exception {
		File file = ResourceUtils.getFile(assetName);
		String fileExtension = FilenameUtils.getExtension(assetName);
		String contentType = "image/png";

		if (fileExtension.toLowerCase() == "xml") {
			contentType = "text/xml";
		}

		return new MockMultipartFile(requestParamName, file.getName(), contentType, loadFileAsBytesArray(file));
	}

	public static byte[] loadFileAsBytesArray(final File file) throws Exception {
		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
		int length = (int) file.length();
		byte[] bytes = new byte[length];
		reader.read(bytes, 0, length);
		reader.close();

		return bytes;
	}
}
