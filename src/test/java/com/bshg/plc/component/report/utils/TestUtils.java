package com.bshg.plc.component.report.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

		return new MockMultipartFile(requestParamName, file.getName(), contentType, readFileAsBytesArray(file));
	}
	
	public static byte[] readFileAsBytesArray(final File file) throws IOException {
		return Files.readAllBytes(Paths.get(file.getAbsolutePath()));
	}
}
