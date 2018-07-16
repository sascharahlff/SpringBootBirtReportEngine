package com.bshg.plc.component.report.utils;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public class ReportUtils {
	public static String getFileExtension(String fileName) {
		String fileExtension = FilenameUtils.getExtension(fileName);

		if (!fileExtension.isEmpty()) {
			return fileExtension;
		}

		return "";
	}

	public static String xxgetFileExtension(MultipartFile file) {
		String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());

		if (!fileExtension.isEmpty()) {
			return fileExtension;
		}

		return "";
	}
}
